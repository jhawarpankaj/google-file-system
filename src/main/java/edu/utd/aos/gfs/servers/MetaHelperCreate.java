package edu.utd.aos.gfs.servers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

	public static void initMetaFile(String fileName, List<ChunkServer> chunkServerNames) {
		File metafile = null;
		String completeFilePath = Nodes.metaServerRootDir() + fileName;
		try {
			metafile = new File(completeFilePath);
			if (metafile.createNewFile()) {
				Logger.info("MetaFile:" + completeFilePath + " created");
			} else
				Logger.info("MetaFile:" + completeFilePath + " already exists under ");// TODO
			String newEntry = GFSReferences.TOTAL_CHUNKS + GFSReferences.MFILE_SEPARATOR + 1;
			newEntry += GFSReferences.NEW_LINE;
			BufferedWriter writer = new BufferedWriter(new FileWriter(completeFilePath));
			newEntry += GFSReferences.CHUNK_PREFIX + 1 + GFSReferences.MFILE_SEPARATOR;
			for (ChunkServer chunk : chunkServerNames)
				newEntry += chunk.getName() + GFSReferences.MCHUNK_SEPARATOR;
			newEntry = newEntry.substring(0, newEntry.length() - 1);
			writer.write(newEntry);
			writer.close();
			Logger.info("MetaFile New Entry Added-" + newEntry);
		} catch (Exception e) {
			Logger.info(e);
		}
	}

	public static List<ChunkServer> get_3RandomChunkServers() {
		int element = random.nextInt(10);
		if (chunkServerCombos != null && chunkServerCombos.size() > 0)
			return chunkServerCombos.get(element);
		else {
			populateChunkServerCombos();
			return chunkServerCombos.get(element);
		}
	}

	public static void forwardCreationToChunks(List<ChunkServer> chunkServers, String fileName, MetaImpl mimpl) {
		String message = GFSReferences.CREATE + GFSReferences.SEND_SEPARATOR;
		message += fileName;// + GFSReferences.SEND_SEPARATOR;
		// message += GFSReferences.CHUNK_PREFIX + 1;
		mimpl.setCreateSentFlag(true);
		for (ChunkServer chunk : chunkServers) {
			Sockets.sendMessage(chunk.getName(), chunk.getPort(), message);
			Logger.info("Forwarded " + message + " CREATE to ChunkServer-" + chunk.getName());
			mimpl.incCreateSentCounter();
			// Logger.info("Message:" + message);
		}
	}

	public static void waitForChunkServerAck(MetaImpl mimpl) {
		while (mimpl.isCreateSentFlag() && mimpl.getCreateSentCounter() > 0) {
			Logger.info("Waiting for CREATE_ACK from the Chunk Servers");
		}
		Logger.info("Received all CREATE_ACKS.");
	}

	public static void sendCreateSuccessClient(String server, String filename) {
		Logger.info("Creation completed, sending Acknowledgement to Client");
		String message = GFSReferences.CREATE_SUC + GFSReferences.SEND_SEPARATOR + filename;
		int port = Nodes.getPortByHostName(server);
		Sockets.sendMessage(server, port, message);
		Logger.info(message + " sent to " + server);

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
		if (mimpl.getCreateSentCounter() == 0) {
			mimpl.setCreateSentFlag(false);
			Logger.info("Received all CREATE_ACKS");
		}
	}

}
