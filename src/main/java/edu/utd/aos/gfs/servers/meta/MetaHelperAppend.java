package edu.utd.aos.gfs.servers.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.tinylog.Logger;

import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class MetaHelperAppend {

	public static void appendOrCreate(String message, String server, MetaImpl mimpl) {
		Logger.info("Deciding whether to APPEND or CREATE&APPEND");
		String tokens[] = message.split(GFSReferences.REC_SEPARATOR);
		String filename = tokens[1];
		int datasize = Integer.valueOf(tokens[2]);
		List<String> lastChunk = getLastChunkDetails(filename);
		int chunksize = Integer.valueOf(lastChunk.get(0));
		int compare = compareDataSize(chunksize, datasize);
		if (compare == 1 || compare == 0) {
			Logger.info("Happy Case: Data can be appended to the last chunk");
			sendAppendToClient(filename, server, lastChunk.get(2), lastChunk.get(3), mimpl, datasize);
		} else {
			Logger.info("New Chunk Needs to be created");
			padWithNull(filename, lastChunk.get(2), lastChunk.get(3), mimpl);
			waitForPadAck(mimpl);
			String newChunkNum = getNewChunkNum(lastChunk.get(2));
			Logger.info("Created new Chunk Number:" + newChunkNum);
			String chunkservers = createBeforeAppend(filename, newChunkNum, mimpl);
			sendAppendToClient(filename, server, newChunkNum, chunkservers, mimpl, datasize);
		}
		Logger.info("Done with the APPEND completely");
	}

	private static String getNewChunkNum(String prevChunkNum) {
		int num = Integer.valueOf(prevChunkNum.substring(prevChunkNum.length() - 1));
		num = num + 1;
		return GFSReferences.CHUNK_PREFIX + num;
	}

	private static List<String> getLastChunkDetails(String filename) {
		Logger.info("Getting last chunk servers for APPEND at file:" + filename);
		List<String> result = new ArrayList<String>();
		if (MetaHelperHeartbeat.metaMap.containsRow(filename)) {
			Map<String, List<String>> map = MetaHelperHeartbeat.metaMap.row(filename);
			int lastchunk = 0;
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				String chunknum = entry.getKey();
				String last = chunknum.substring(chunknum.length() - 1);
				int templast = Integer.valueOf(last);
				if (templast > lastchunk) {
					lastchunk = templast;
					String size = entry.getValue().get(0);
					String version = entry.getValue().get(1);
					String chunkservers = entry.getValue().get(2);
					result.clear();
					result.add(size);
					result.add(version);
					result.add(chunknum);
					result.add(chunkservers);
				}
			}
			Logger.info("Last Chunk is:" + result.get(2) + " ,Last Chunk Size:" + result.get(0)
					+ " ,Last Chunk Version:" + result.get(1) + ", Last Chunk Servers:" + result.get(3));
		} else {
			Logger.error("Filename:" + filename + " not found in MetaInfo, returning null");
		}
		return result;
	}

	private static void sendAppendToClient(String filename, String server, String chunknum, String chunkservers,
			MetaImpl mimpl, int datasize) {
		String message = GFSReferences.APPEND + GFSReferences.SEND_SEPARATOR;
		message += filename + GFSReferences.SEND_SEPARATOR;
		message += chunknum + GFSReferences.SEND_SEPARATOR;
		chunkservers = chooseAliveServer(chunkservers, mimpl, chunknum, filename);
		message += chunkservers + GFSReferences.SEND_SEPARATOR;
		message += datasize;
		int port = Nodes.getPortByHostName(server);
		Sockets.sendMessage(server, port, message);
		Logger.info("Sent APPEND to client, message:" + message);
		mimpl.setAppendSentFlag(true);
		Logger.info("Waiting for APPEND_ACK_META from client:" + server);
		waitForAppendAck(mimpl);

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
		Logger.info("ALIVE servers for append are:" + server);
		return server;

	}

	private static void waitForAppendAck(MetaImpl mimpl) {
		while (mimpl.isAppendSentFlag()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Logger.info("Received APPEND_ACK_META from Client. Removing from queue");

	}

	public static void handleAppendAck(String server, MetaImpl cimpl) {
		Logger.info("Received APPEND_ACK_META from Client:" + server);
		cimpl.setAppendSentFlag(false);

	}

	private static String createBeforeAppend(String filename, String newChunkNum, MetaImpl mimpl) {
		Logger.info("Asking Random 3 Chunks to create new chunk" + "," + filename + ":" + newChunkNum);
		List<ChunkServer> chunkServers = MetaHelperCreate.get_3RandomChunkServers(mimpl);
		forwardNewChunkCreationToChunks(chunkServers, filename, newChunkNum, mimpl);
		waitForNewChunkServerAck(mimpl);
		String chunks = "";
		for (ChunkServer chunk : chunkServers)
			chunks += chunk.getName() + ",";
		chunks = chunks.substring(0, chunks.length() - 1);
		Logger.info("All chunks have been created, proceeding with Append now, Chunks:" + chunks);
		return chunks;
	}

	public static void forwardNewChunkCreationToChunks(List<ChunkServer> chunkServers, String fileName, String chunknum,
			MetaImpl mimpl) {
		Logger.info("Sending CREATE_CHUNK to servers");
		String message = GFSReferences.CREATE_CHUNK + GFSReferences.SEND_SEPARATOR;
		message += fileName + GFSReferences.SEND_SEPARATOR;
		message += chunknum;
		mimpl.setCreateChunkSentFlag(true);
		for (ChunkServer chunk : chunkServers) {
			Sockets.sendMessage(chunk.getName(), chunk.getPort(), message);
			mimpl.incCreateChunkSentCounter();
		}
		Logger.info("Send All CREATE_CHUNK to servers");
	}

	public static void waitForNewChunkServerAck(MetaImpl mimpl) {
		Logger.info("Waiting for CREATE_CHUNK_ACK from the Chunk Servers");
		while (mimpl.isCreateChunkSentFlag() && mimpl.getCreateChunkSentCounter() > 0) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Logger.info("Received all CREATE_CHUNK_ACKs.");
	}

	public static void handleChunkCreateAck(String server, MetaImpl mimpl) {
		Logger.info("Received CHUNK_CREATE_ACK from " + server + " Reducing the counter.");
		mimpl.decCreateChunkSentCounter();
		if (mimpl.getCreateChunkSentCounter() == 0 || mimpl.getCreateChunkSentCounter() < 0) {
			mimpl.setCreateChunkSentFlag(false);
			mimpl.setCreateChunkSentCounter(0);
			Logger.info("Received all CHUNK_CREATE_ACKs");
		}
	}

	private static void padWithNull(String filename, String chunknum, String chunkservers, MetaImpl mimpl) {
		Logger.info("Asking chunk servers to PAD_NULL");
		String message = GFSReferences.PAD_NULL + GFSReferences.SEND_SEPARATOR;
		message += filename + GFSReferences.SEND_SEPARATOR;
		message += chunknum;
		mimpl.setPadSentFlag(true);
		String chunks[] = chunkservers.split(",");
		for (String chunk : chunks) {
			Sockets.sendMessage(chunk, Nodes.getPortByHostName(chunk), message);
			mimpl.incPadSentCounter();
		}
		Logger.info("Sent all ChunkServers PAD_NULL");
	}

	public static void waitForPadAck(MetaImpl mimpl) {
		Logger.info("Waiting for PAD_ACK from the Chunk Servers");
		while (mimpl.isPadSentFlag() && mimpl.getPadSentCounter() > 0) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Logger.info("Received all PAD_ACKs.");
	}

	public static void handlePadAck(String server, MetaImpl mimpl) {
		Logger.info("Received PAD_ACK from " + server + " Reducing the counter.");
		mimpl.decPadSentCounter();
		if (mimpl.getPadSentCounter() == 0 || mimpl.getPadSentCounter() < 0) {
			mimpl.setPadSentFlag(false);
			mimpl.setPadSentCounter(0);
			Logger.info("Received all PAD_ACKs");
		}
	}

	private static int compareDataSize(int chunksize, int datasize) {
		int remaining = GFSReferences.CHUNK_SIZE - chunksize;
		Logger.info("Chunksize: " + chunksize + " Datasize: " + datasize + " Remaining size: " + remaining);
		if (remaining > datasize)
			return 1;
		else if (remaining == datasize)
			return 0;
		else
			return -1;
	}
}
