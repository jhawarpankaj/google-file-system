package edu.utd.aos.gfs.servers;

import org.tinylog.Logger;

import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class ClientHelper {

	public static void forwardReadToChunk(String received) {
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
		Logger.info("Sent a READ message to " + chunkserver);
		Logger.info("Message sent:" + message);
	}

}
