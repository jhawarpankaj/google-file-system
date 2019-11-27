package edu.utd.aos.gfs.servers.meta;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import org.tinylog.Logger;

import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class MetaHelperAppend {

	public static String getLastChunk(String fileName) {
		BufferedReader reader;
		String dir = Nodes.metaServerRootDir();
		String completeFilePath = dir + fileName;
		String lastline = "", result = "";
		try {
			reader = new BufferedReader(new FileReader(completeFilePath));
			String line = reader.readLine();
			while (line != null) {
				lastline = line;
				line = reader.readLine();
			}
			reader.close();
			String tokens[] = lastline.split(GFSReferences.MFILE_SEPARATOR);
			result = tokens[1];
		} catch (Exception e) {
			Logger.info(e);
		}
		return result;
	}

	public static boolean canAppendLastChunk() {
		boolean status = false;
		// pick from map
		// int compare=compareDataSize(chunksize, datasize)
		// if can append
		// send append with details to client
		// else send create to 3 random chunks
		// wait for responses
		// send append to clientss
		return status;
	}

	public static void appendOrCreate(String message, String server, MetaImpl mimpl) {
		String tokens[] = message.split(GFSReferences.REC_SEPARATOR);
		// APPEND||file1||28||2019-11-27 14:44:22.062
		String filename = tokens[1];
		int datasize = Integer.valueOf(tokens[2]);
		int chunksize = 0;// TODO getlastchunksize, store last chunks
		int compare = compareDataSize(chunksize, datasize);
		if (compare == 1) {
			sendAppendToClient(filename, server);
		} else {
			padWithNull("", "");
			waitForPadAck(mimpl);
			createBeforeAppend(filename, mimpl);
			sendAppendToClient(filename, server);
		}
	}

	// sunny day case: data can be
	private static void sendAppendToClient(String filename, String server) {
		String message = GFSReferences.APPEND + GFSReferences.SEND_SEPARATOR;
		message += filename + GFSReferences.SEND_SEPARATOR;
		// TODO-last chunkname and chunkservers
		int port = Nodes.getPortByHostName(server);
		Sockets.sendMessage(server, port, message);
	}

	private static void createBeforeAppend(String filename, MetaImpl mimpl) {
		// Logger.info("Received CREATE from " + ci.getHostname());
		String fileToCreate = filename;// MetaHelperCreate.getFileName(message);
		List<ChunkServer> chunkServers = MetaHelperCreate.get_3RandomChunkServers();
		// MetaHelperCreate.initMetaFile(fileToCreate, chunkServers);// TODO
		MetaHelperCreate.forwardCreationToChunks(chunkServers, fileToCreate, mimpl);
		MetaHelperCreate.waitForChunkServerAck(mimpl);
		// MetaHelperCreate.sendCreateSuccessClient(ci.getHostname(), fileToCreate);
		// mimpl.deleteFromDeferredQueue();
	}

	private static void padWithNull(String filename, String chunknum) {// TODO get chunkserver list
		String message = GFSReferences.PAD_NULL + GFSReferences.SEND_SEPARATOR;
		message += filename + GFSReferences.SEND_SEPARATOR;
		message += chunknum;
		// TODO sent to chunk server
		// increment counter; mimpl.incCreateSentCounter();
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
		Logger.info("Received all PAD_ACK.");
	}

	private static int compareDataSize(int chunksize, int datasize) {
		if (chunksize > datasize)
			return 1;
		else
			return 0;
	}
}
