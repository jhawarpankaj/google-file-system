package edu.utd.aos.gfs.servers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class MetaHelperRead {
	public static List<String> computeChunkFromOffset(String offset) {
		Integer offsetInt = Integer.parseInt(String.valueOf(offset));
		int chunkNum = offsetInt / GFSReferences.CHUNK_SIZE + 1;
		int chunkOffset = offsetInt % GFSReferences.CHUNK_SIZE;
		List<String> readMsgDetails = new ArrayList<String>();
		readMsgDetails.add(0, GFSReferences.CHUNK_PREFIX + String.valueOf(chunkNum));
		readMsgDetails.add(1, String.valueOf(chunkOffset));
		return readMsgDetails;
	}

	public static String getChunkServersToRead(String fileName, String chunkNum) {
		BufferedReader reader;
		String dir = Nodes.metaServerRootDir();
		String completeFilePath = dir + fileName;
		String result = "";
		try {
			reader = new BufferedReader(new FileReader(completeFilePath));
			String line = reader.readLine();
			while (line != null) {
				if (line.startsWith(chunkNum)) {
					String tokens[] = line.split(GFSReferences.MFILE_SEPARATOR);
					result = tokens[1];
					break;
				}
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			Logger.info(e);
		}
		return result;
	}

	public static String generateReadResponse(String fileName, List<String> chunkDetails, String chunkServers) {
		String result = "";
		result += GFSReferences.READ + GFSReferences.SEND_SEPARATOR;
		result += fileName + GFSReferences.SEND_SEPARATOR;
		result += chunkDetails.get(0) + GFSReferences.SEND_SEPARATOR;
		result += chunkDetails.get(1) + GFSReferences.SEND_SEPARATOR;
		result += chunkServers;
		return result;

	}

	public static void forwardReadToClient(String response, String name) {
		int port = Nodes.getPortByHostName(name);
		Sockets.sendMessage(name, port, response);
	}
}
