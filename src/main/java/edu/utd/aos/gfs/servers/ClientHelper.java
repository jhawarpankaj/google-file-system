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
		// Sockets.sendMessage(chunkserver, chunkserverPort, message);
		Logger.info("Sending a READ message to " + chunkserver);
		Logger.info("Message sent:" + message);
	}

	public static void handleCreateResponse(String command) {
		Logger.info("Received :" + command);
	}

	public static void handleReadResponse(String message) {
		Logger.info("Received READ response from chunk");
		String token[] = message.split(GFSReferences.REC_SEPARATOR);
		String file = token[1];
		String content = token[2];
		Logger.info("READ_CONTENT for file:" + file + " is:" + content);
		// TODO display to terminal
	}

	private static String generateRandomWord() {
		String randomString = "";
		Random random = new Random();
		char[] word = new char[random.nextInt(8) + 3];
		for (int j = 0; j < word.length; j++) {
			word[j] = (char) ('a' + random.nextInt(26));
		}
		randomString = new String(word);
		// System.out.println(randomString);
		return randomString;
	}

	public static void handleAppendResponseFromMeta(String message) {
		String tokens[] = message.split(GFSReferences.REC_SEPARATOR);
		String filename = tokens[1];
		String chunknum = tokens[2];
		String chunkservers[] = tokens[3].split(",");
		String forwardMsg = GFSReferences.APPEND + GFSReferences.SEND_SEPARATOR;
		forwardMsg += filename + GFSReferences.SEND_SEPARATOR;
		forwardMsg += chunknum + GFSReferences.SEND_SEPARATOR;
		forwardMsg += generateRandomWord();
		for (String chunkserver : chunkservers) {
			int port = Nodes.getPortByHostName(chunkserver);
			Sockets.sendMessage(chunkserver, port, message);
			// increment counter TODO
		}
	}

}
