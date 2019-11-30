package edu.utd.aos.gfs.servers.meta.thread;

import java.util.List;

import org.tinylog.Logger;

import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.servers.meta.ClientInfo;
import edu.utd.aos.gfs.servers.meta.MetaHelperAppend;
import edu.utd.aos.gfs.servers.meta.MetaHelperCreate;
import edu.utd.aos.gfs.servers.meta.MetaHelperRead;
import edu.utd.aos.gfs.servers.meta.MetaImpl;
import edu.utd.aos.gfs.servers.meta.MetaQueue;
import edu.utd.aos.gfs.utils.Helper;

public class MetaQueueReader extends Thread {
	MetaImpl mimpl;

	public MetaQueueReader(MetaImpl mimpl) {
		this.mimpl = mimpl;
	}

	@Override
	public void run() {
		Logger.info("Queue-Reader Thread Started");
		while (true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				if (mimpl.getQueuedRequest().size() > 0) {
					MetaQueue chosenRequest = mimpl.chooseFromDeferredQueue();
					ClientInfo ci = chosenRequest.getClient();
					String message = chosenRequest.getMessage();
					String command = Helper.getCommand(message);
					switch (command) {
					case GFSReferences.CREATE:
						Logger.info("Received CREATE from " + ci.getHostname());
						String fileToCreate = MetaHelperCreate.getFileName(message);
						List<ChunkServer> chunkServers = MetaHelperCreate.get_3RandomChunkServers(mimpl);
						MetaHelperCreate.forwardCreationToChunks(chunkServers, fileToCreate, mimpl);
						MetaHelperCreate.waitForChunkServerAck(mimpl);
						MetaHelperCreate.sendCreateSuccessClient(ci.getHostname(), fileToCreate);
						mimpl.deleteFromDeferredQueue();
						break;

					case GFSReferences.READ:
						Logger.info("Received READ from " + ci.getHostname());
						String fileToRead = Helper.getMessage(message);
						String offset = Helper.getParamThree(message);
						List<String> chunkDetails = MetaHelperRead.computeChunkFromOffset(offset);
						String chunkServersList = MetaHelperRead.getChunkServersToRead(fileToRead, chunkDetails.get(0));
						String response = MetaHelperRead.generateReadMsgForClient(fileToRead, chunkDetails,
								chunkServersList, mimpl);
						MetaHelperRead.forwardReadToClient(response, ci.getHostname());
						mimpl.deleteFromDeferredQueue();
						break;

					case GFSReferences.APPEND:
						Logger.info("Received APPEND from " + ci.getHostname());
						MetaHelperAppend.appendOrCreate(message, ci.getHostname(), mimpl);
						mimpl.deleteFromDeferredQueue();
						break;

					default:
						throw new GFSException("Unidentified input: " + command + " received on META server!!");
					}
				}
			} catch (Exception e) {
				Logger.error("Error while performing client request: " + e);
			}
		}
	}
}
