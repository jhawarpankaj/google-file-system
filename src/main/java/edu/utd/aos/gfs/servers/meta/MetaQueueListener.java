package edu.utd.aos.gfs.servers.meta;

import java.util.List;

import org.tinylog.Logger;

import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;

public class MetaQueueListener extends Thread {
	MetaImpl mimpl;

	public MetaQueueListener(MetaImpl mimpl) {
		this.mimpl = mimpl;
	}

	@Override
	public void run() {
		Logger.info("Queue Listener Thread Started");
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
						List<ChunkServer> chunkServers = MetaHelperCreate.get_3RandomChunkServers();
						// MetaHelperCreate.initMetaFile(fileToCreate, chunkServers);// TODO
						MetaHelperCreate.forwardCreationToChunks(chunkServers, fileToCreate, mimpl);
						MetaHelperCreate.waitForChunkServerAck(mimpl);
						MetaHelperCreate.sendCreateSuccessClient(ci.getHostname(), fileToCreate);
						mimpl.deleteFromDeferredQueue();
						break;

					/*
					 * case GFSReferences.READ: String fileToRead = Helper.getMessage(received);
					 * String offset = Helper.getParamThree(received); List<String> chunkDetails =
					 * MetaHelperRead.computeChunkFromOffset(offset); String chunkServersList =
					 * MetaHelperRead.getChunkServersToRead(fileToRead, chunkDetails.get(0)); String
					 * response = MetaHelperRead.generateReadResponse(fileToRead, chunkDetails,
					 * chunkServersList); MetaHelperRead.forwardReadToClient(response, server);
					 * break;
					 * 
					 * case GFSReferences.APPEND: String fileToAppend = Helper.getMessage(received);
					 * String appendContent = Helper.getParamThree(received); break;
					 */

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
