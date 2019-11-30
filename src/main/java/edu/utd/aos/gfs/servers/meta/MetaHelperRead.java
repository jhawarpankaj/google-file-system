package edu.utd.aos.gfs.servers.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class MetaHelperRead {
	public static List<String> computeChunkFromOffset(String offset) {
		Integer offsetInt = Integer.parseInt(String.valueOf(offset));
		int chunkNum = offsetInt / GFSReferences.CHUNK_SIZE + 1;
		int chunkOffset = offsetInt % GFSReferences.CHUNK_SIZE;
		// index0-chunknum; index1-offsetInchunk
		List<String> readMsgDetails = new ArrayList<String>();
		readMsgDetails.add(0, GFSReferences.CHUNK_PREFIX + String.valueOf(chunkNum));
		readMsgDetails.add(1, String.valueOf(chunkOffset));
		return readMsgDetails;
	}

	public static String getChunkServersToRead(String fileName, String chunkNum) {
		Logger.info("Returning the chunk servers for READ");
		if (MetaHelperHeartbeat.metaMap.containsRow(fileName)) {
			Map<String, List<String>> map = MetaHelperHeartbeat.metaMap.row(fileName);
			List<String> chunkdetails = map.get(chunkNum);
			String chunkservers = chunkdetails.get(2);
			return chunkservers;
		} else {
			Logger.error("Filename:" + fileName + " not found in MetaInfo, returning null");
			return null;
		}
	}

	public static String generateReadMsgForClient(String fileName, List<String> chunkDetails, String chunkServers,
			MetaImpl mimpl) {
		String result = "";
		result += GFSReferences.READ + GFSReferences.SEND_SEPARATOR;
		result += fileName + GFSReferences.SEND_SEPARATOR;
		result += chunkDetails.get(0) + GFSReferences.SEND_SEPARATOR;
		result += chunkDetails.get(1) + GFSReferences.SEND_SEPARATOR;
		chunkServers = chooseAliveServer(chunkServers, mimpl, chunkDetails.get(0), fileName);
		result += chunkServers;
		Logger.info("Generated READ message to send to client");
		return result;

	}

	private static String chooseAliveServer(String servers, MetaImpl mimpl, String chunknum, String filename) {
		String server = "";
		String serverlist[] = servers.split(",");
		for (String s : serverlist) {
			if (!mimpl.getChunkLiveness().containsKey(s)) {
				server = server + s + ",";
			}
		}
		if (server.isEmpty())
			Logger.info("All chunk servers " + chunknum + "-" + filename + " are down. Not sending any server");
		else
			server = server.substring(0, server.length() - 1);
		Logger.info("ALIVE servers for READ are:" + server);
		return server;

	}

	public static void forwardReadToClient(String response, String name) {
		Logger.info("Forwarded READ to client:" + name);
		int port = Nodes.getPortByHostName(name);
		Sockets.sendMessage(name, port, response);
	}
}
