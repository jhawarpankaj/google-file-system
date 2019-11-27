package edu.utd.aos.gfs.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.tinylog.Logger;

import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;

public class MetaListener extends Thread {
	final Socket worker;
	final DataInputStream dis;
	final DataOutputStream dos;
	MetaImpl mimpl;
	public static final ReentrantLock lock = new ReentrantLock();

	public MetaListener(Socket worker, DataInputStream dis, DataOutputStream dos, MetaImpl mimpl) {
		this.worker = worker;
		this.dis = dis;
		this.dos = dos;
		this.mimpl = mimpl;
	}

	@Override
	public void run() {
		try {
			// message received on socket.
			String received = dis.readUTF();
			// server from which the message has come.
			String server = this.worker.getInetAddress().getHostName();

			Logger.info("Received message: " + received);
			Logger.info("Adding it to the PriorityQueue, queue size:" + mimpl.getQueuedRequest().size());
			String command = Helper.getCommand(received);
			if (Helper.isQueueableMessage(command)) {
				mimpl.addToDeferredQueue(received);// LOGIC TODO
				received = mimpl.chooseFromDeferredQueue();
				command = Helper.getCommand(received);
			} else
				Logger.info("Message is not  a Queueable Message, beginning to process");
			switch (command) {

			case GFSReferences.HEARTBEAT:
				String message = Helper.getMessage(received);
				Logger.info("Received heartbeat, not doing anything for now");
				// JsonObject heartbeatJson = Helper.getParsedHeartBeat(message);
				// Logger.debug("Parsed heart beat message: " + heartbeatJson); // TODO
				// Helper.iterateHeartBeat(server, heartbeatJson);
				break;

			case GFSReferences.CREATE:
				Logger.info("Received CREATE from " + server);
				String fileToCreate = MetaHelperCreate.getFileName(received);
				List<ChunkServer> chunkServers = MetaHelperCreate.get_3RandomChunkServers();
				// MetaHelperCreate.initMetaFile(fileToCreate, chunkServers);// TODO
				MetaHelperCreate.forwardCreationToChunks(chunkServers, fileToCreate, mimpl);
				MetaHelperCreate.waitForChunkServerAck(mimpl);
				MetaHelperCreate.sendCreateSuccessClient(server, fileToCreate);// TODO PICK NEXT?
				break;
			case GFSReferences.CREATE_ACK:
				MetaHelperCreate.handleCreateAck(server, mimpl);
				break;

			case GFSReferences.READ:
				String fileToRead = Helper.getMessage(received);
				String offset = Helper.getParamThree(received);
				List<String> chunkDetails = MetaHelperRead.computeChunkFromOffset(offset);
				String chunkServersList = MetaHelperRead.getChunkServersToRead(fileToRead, chunkDetails.get(0));
				String response = MetaHelperRead.generateReadResponse(fileToRead, chunkDetails, chunkServersList);
				MetaHelperRead.forwardReadToClient(response, server);
				break;

			case GFSReferences.APPEND:
				String fileToAppend = Helper.getMessage(received);
				String appendContent = Helper.getParamThree(received);
				break;

			default:
				throw new GFSException("Unidentified input: " + command + " received on META server!!");
			}

		} catch (Exception e) {
			Logger.error("Error while performing client request: " + e);
		}
	}
}
