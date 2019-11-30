package edu.utd.aos.gfs.servers.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.tinylog.Logger;

import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class MetaHelperCreate {
	public static List<List<ChunkServer>> chunkServerCombos = new ArrayList<List<ChunkServer>>();
	public static Random random = new Random();

	public static List<ChunkServer> get_3RandomChunkServers(MetaImpl mimpl) {
		boolean next = true;
		List<ChunkServer> chunks = null;
		if (chunkServerCombos != null && chunkServerCombos.size() > 0) {
			while (next) {
				int element = random.nextInt(10);
				chunks = chunkServerCombos.get(element);
				boolean flag = false;
				for (ChunkServer chunk : chunks) {
					if (mimpl.getChunkLiveness().containsKey(chunk.getName())) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					next = false;
				}
			}
		} else {
			populateChunkServerCombos();
			while (next) {
				int element = random.nextInt(10);
				chunks = chunkServerCombos.get(element);
				boolean flag = false;
				for (ChunkServer chunk : chunks) {
					if (mimpl.getChunkLiveness().containsKey(chunk.getName())) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					next = false;
				}
			}
		}
		String chunkservers = "";
		for (ChunkServer chunk : chunks)
			chunkservers += chunk + ",";
		Logger.info("Alive Servers for CREATE:" + chunkservers.substring(0, chunkservers.length() - 1));
		return chunks;
	}

	public static void forwardCreationToChunks(List<ChunkServer> chunkServers, String fileName, MetaImpl mimpl) {
		String message = GFSReferences.CREATE + GFSReferences.SEND_SEPARATOR;
		message += fileName;
		mimpl.setCreateSentFlag(true);
		for (ChunkServer chunk : chunkServers) {
			Sockets.sendMessage(chunk.getName(), chunk.getPort(), message);
			mimpl.incCreateSentCounter();
		}
	}

	public static void waitForChunkServerAck(MetaImpl mimpl) {
		Logger.info("Waiting for CREATE_ACK from the Chunk Servers");
		while (mimpl.isCreateSentFlag() && mimpl.getCreateSentCounter() > 0) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Logger.info("Received all CREATE_ACKS.");
	}

	public static void sendCreateSuccessClient(String server, String filename) {
		Logger.info("Creation completed, sending Acknowledgement to Client");
		String message = GFSReferences.CREATE_SUC + GFSReferences.SEND_SEPARATOR + filename;
		int port = Nodes.getPortByHostName(server);
		Sockets.sendMessage(server, port, message);
	}

	private static void randomServer(List<ChunkServer> arr, List<ChunkServer> data, int start, int end, int index,
			int r) {
		if (index == r) {
			List<ChunkServer> li = new ArrayList<ChunkServer>();
			for (int j = 0; j < r; j++)
				li.add(data.get(j));
			chunkServerCombos.add(li);
			return;
		}
		for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
			data.add(index, arr.get(i));
			randomServer(arr, data, i + 1, end, index + 1, r);
		}
	}

	private static void populateChunkServerCombos() {
		List<ChunkServer> arr = Nodes.chunkServersList();
		List<ChunkServer> data = new ArrayList<ChunkServer>(3);
		randomServer(arr, data, 0, 4, 0, 3);
	}

	public static String getFileName(String message) {
		return message.split(GFSReferences.REC_SEPARATOR)[1];
	}

	public static String getRequestTimestamp(String message) {
		return message.split(GFSReferences.REC_SEPARATOR)[2];
	}

	public static void handleCreateAck(String server, MetaImpl mimpl) {
		Logger.info("Received CREATE_ACK from " + server + " Reducing the counter.");
		mimpl.decCreateSentCounter();
		if (mimpl.getCreateSentCounter() == 0 || mimpl.getCreateSentCounter() < 0) {
			mimpl.setCreateSentFlag(false);
			mimpl.setCreateSentCounter(0);
			Logger.info("Received all CREATE_ACKS");
		}
	}

}
