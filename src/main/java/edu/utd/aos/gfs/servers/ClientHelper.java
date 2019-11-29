package edu.utd.aos.gfs.servers;

import java.util.Random;

import org.tinylog.Logger;

import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class ClientHelper {

	public static void forwardReadToChunk(String received) {
		Logger.info("Prepping to forward READ to Chunks");
		String tokens[] = received.split(GFSReferences.REC_SEPARATOR);
		String command = tokens[0];
		String filename = tokens[1];
		String chunknum = tokens[2];
		String offset = tokens[3];
		String chunkservers = tokens[4];
		String chunkserver = chunkservers.split(",")[0];
		int chunkserverPort = Nodes.getPortByHostName(chunkserver);
		String message = command + GFSReferences.SEND_SEPARATOR;
		message += filename + GFSReferences.SEND_SEPARATOR;
		message += chunknum + GFSReferences.SEND_SEPARATOR;
		message += offset;
		Sockets.sendMessage(chunkserver, chunkserverPort, message);
		Logger.info("Sending a READ message to " + chunkserver);
		Logger.info("Message sent:" + message);
	}

	public static void handleCreateResponse(String command) {
		Logger.info("Received :" + command);
	}

	public static void handleReadResponse(String message) {
		Logger.info("Received READ response from chunk: " + message);
		String token[] = message.split(GFSReferences.REC_SEPARATOR);
		String file = token[1];
		String content = token[2];
		Logger.info("READ_CONTENT for file:" + file + " is:" + content);
		// TODO display to terminal
	}

	private static String generateRandomWord(int datasize) {
		String randomString = "";
		Random random = new Random();
		char[] word = new char[datasize];
		for (int j = 0; j < word.length; j++) {
			word[j] = (char) ('a' + random.nextInt(26));
		}
		randomString = new String(word);
		return randomString;
	}

	public static void forwardAppendToChunk(String message, ClientImpl cimpl) {
		Logger.info("Initiating 2-Phase Commit from Client");
		sendAppendToChunks(message, cimpl);
		waitForReadyToAppendAck(cimpl);
		sendCommitToChunks(cimpl);
		waitForCommitAck(cimpl);
		sendAppendAckMeta(message);
		Logger.info("APPEND has been completed");
		cimpl.setAppendMessage("");
	}

	private static void sendAppendAckMeta(String message) {
		Logger.info("Completed 2-Phase Commit from Client, sending APPEND_ACK_META to Meta server");
		String tokens[] = message.split(GFSReferences.REC_SEPARATOR);
		String filename = tokens[1];
		String msg = GFSReferences.APPEND_ACK_META + GFSReferences.SEND_SEPARATOR;
		msg += filename;
		Sockets.sendMessage(Nodes.metaServerName(), Nodes.metaServerPort(), msg);
	}

	private static void sendAppendToChunks(String message, ClientImpl cimpl) {
		Logger.info("Beginning to send APPEND to chunk servers:" + message);
		String tokens[] = message.split(GFSReferences.REC_SEPARATOR);
		String filename = tokens[1];
		String chunknum = tokens[2];
		String chunkservers[] = tokens[3].split(",");
		int datasize = Integer.parseInt(tokens[4]);
		String forwardMsg = GFSReferences.APPEND + GFSReferences.SEND_SEPARATOR;
		forwardMsg += filename + GFSReferences.SEND_SEPARATOR;
		forwardMsg += chunknum + GFSReferences.SEND_SEPARATOR;
		forwardMsg += generateRandomWord(datasize);
		cimpl.setAppendMessage(message);
		cimpl.setAppendSentFlag(true);
		for (String chunkserver : chunkservers) {
			int port = Nodes.getPortByHostName(chunkserver);
			Sockets.sendMessage(chunkserver, port, forwardMsg);
			cimpl.incAppendSentCounter();
		}
		Logger.info("Sent all APPENDs to the chunk servers");
	}

	public static void waitForReadyToAppendAck(ClientImpl cimpl) {
		Logger.info("Waiting for READY_TO_APPEND from the Chunk Servers");
		while (cimpl.isAppendSentFlag() && cimpl.getAppendSentCounter() > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Logger.info("Received all READY_TO_APPEND ACKS.");
	}

	public static void handleReadyToAppendResponse(String received, String server, ClientImpl cimpl) {
		Logger.info("Received READY_TO_APPEND from " + server + " Reducing the counter.");
		cimpl.decAppendSentCounter();
		if (cimpl.getAppendSentCounter() == 0 || cimpl.getAppendSentCounter() < 0) {
			cimpl.setAppendSentFlag(false);
			cimpl.setAppendSentCounter(0);
			Logger.info("Received all READY_TO_APPEND ACKS.");
		}
	}

	public static void handleCommitAck(String received, String server, ClientImpl cimpl) {
		Logger.info("Received COMMIT_ACK from " + server + " Reducing the counter.");
		cimpl.decCommitSentCounter();
		if (cimpl.getCommitSentCounter() == 0 || cimpl.getCommitSentCounter() < 0) {
			cimpl.setCommitSendFlag(false);
			cimpl.setCommitSentCounter(0);
			Logger.info("Received all COMMIT_ACK.");
		}
	}

	private static void sendCommitToChunks(ClientImpl cimpl) {
		Logger.info("Sending COMMIT to chunks");
		String message = cimpl.getAppendMessage();
		String tokens[] = message.split(GFSReferences.REC_SEPARATOR);
		String filename = tokens[1];
		String chunknum = tokens[2];
		String chunkservers[] = tokens[3].split(",");
		String commit = GFSReferences.COMMIT + GFSReferences.SEND_SEPARATOR;
		commit += filename + GFSReferences.SEND_SEPARATOR;
		commit += chunknum;
		cimpl.setCommitSendFlag(true);
		for (String chunkserver : chunkservers) {
			int port = Nodes.getPortByHostName(chunkserver);
			Sockets.sendMessage(chunkserver, port, commit);
			cimpl.incCommitSentCounter();
		}
		Logger.info("Finished sending COMMIT to all chunks");
	}

	public static void waitForCommitAck(ClientImpl cimpl) {
		Logger.info("Waiting for COMMIT_ACK from the Chunk Servers");
		while (cimpl.isCommitSendFlag() && cimpl.getCommitSentCounter() > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Logger.info("Received all COMMIT_ACKs.");
	}

}
