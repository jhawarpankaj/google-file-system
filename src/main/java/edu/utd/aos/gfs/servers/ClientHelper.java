package edu.utd.aos.gfs.servers;

import java.sql.Timestamp;
import java.util.Random;

import org.tinylog.Logger;

import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class ClientHelper {

	public static void forwardReadToChunk(String received, ClientImpl cimpl) {
		Logger.info("Prepping to forward READ to Chunks");
		cimpl.setReceivedReadResponse(false);
		String tokens[] = received.split(GFSReferences.REC_SEPARATOR);
		String command = tokens[0];
		String filename = tokens[1];
		String chunknum = tokens[2];
		String offset = tokens[3];
		String chunkservers = tokens[4];
		String chunkserversarr[] = chunkservers.split(",");
		for (String chunkserver : chunkserversarr) {
			if (connectToChunkForRead(chunkserver, filename, chunknum, command, offset, cimpl)) {
				Logger.info("READ Send and Response Successful");
				return;
			} else {
				Logger.info("Trying another server for READ");
			}
		}
		Logger.info("None of the provided servers are available for read");
		Logger.info("READ FAILED. Try again after sometime");
		// String chunkserver = chunkservers.split(",")[0];
		// int chunkserverPort = Nodes.getPortByHostName(chunkserver);
		// String message = command + GFSReferences.SEND_SEPARATOR;
		// message += filename + GFSReferences.SEND_SEPARATOR;
		// message += chunknum + GFSReferences.SEND_SEPARATOR;
		// message += offset;
		// Sockets.sendMessage(chunkserver, chunkserverPort, message);
		// Logger.info("Sending a READ message to " + chunkserver);
		// Logger.info("Message sent:" + message);
	}

	private static boolean connectToChunkForRead(String chunkserver, String filename, String chunknum, String command,
			String offset, ClientImpl cimpl) {
		int diff = 0;
		Timestamp start = Helper.getTimestamp();
		int chunkserverPort = Nodes.getPortByHostName(chunkserver);
		String message = command + GFSReferences.SEND_SEPARATOR;
		message += filename + GFSReferences.SEND_SEPARATOR;
		message += chunknum + GFSReferences.SEND_SEPARATOR;
		message += offset;
		Logger.info("Sending a READ message to " + chunkserver);
		Sockets.sendMessage(chunkserver, chunkserverPort, message);
		Logger.info("Waiting for READ Response from chunkserver:" + chunkserver);
		while (diff <= GFSReferences.TIMEOUT && !cimpl.isReceivedReadResponse()) {
			Timestamp current = Helper.getTimestamp();
			diff = Helper.getTimeDifference(start, current);
		}
		if (cimpl.isReceivedReadResponse()) {
			Logger.info("Received a READ Response");
			return true;
		} else {
			// Logger.info("ChunkServer:" + chunkserver + " may have gone down, retrying
			// with another server");
			return false;
		}
	}

	public static void handleCreateResponse(String command) {
		Logger.info("Received :" + command);
	}

	public static void handleReadResponse(String message, ClientImpl cimpl) {
		Logger.info("Received READ response from chunk: " + message);
		cimpl.setReceivedReadResponse(true);
		String token[] = message.split(GFSReferences.REC_SEPARATOR);
		if (token.length < 3) {
			Logger.info("No content available from the file to READ");

		} else {
			String file = token[1];
			String content = token[2];
			Logger.info("READ_CONTENT for file:" + file + " is:" + content);
		}
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
