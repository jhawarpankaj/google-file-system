package edu.utd.aos.gfs.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.tinylog.Logger;

import com.google.gson.JsonObject;

import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;

public class MetaListener extends Thread {
	final Socket worker;
	final DataInputStream dis;
	final DataOutputStream dos;
	public static final ReentrantLock lock = new ReentrantLock();

	public MetaListener(Socket worker, DataInputStream dis, DataOutputStream dos) {
		this.worker = worker;
		this.dis = dis;
		this.dos = dos;
	}

	@Override
	public void run() {
		try {
			// message received on socket.
			String received = dis.readUTF();
			// server from which the message has come.
			String server = this.worker.getInetAddress().getHostName();

			Logger.debug("Received message: " + received);

			String command = Helper.getCommand(received);

			switch (command) {

			case GFSReferences.HEARTBEAT:
				String message = Helper.getMessage(received);
				JsonObject heartbeatJson = Helper.getParsedHeartBeat(message);
				Logger.debug("Parsed heart beat message: " + heartbeatJson); // TODO
				Helper.iterateHeartBeat(server, heartbeatJson);
				break;

			case GFSReferences.CREATE:
				String fileToCreate = Helper.getMessage(received);
				List<ChunkServer> chunkServers = MetaHelperCreate.get_3RandomChunkServers();
				MetaHelperCreate.initMetaFile(fileToCreate, chunkServers);
				MetaHelperCreate.forwardCreationToChunks(chunkServers, fileToCreate);
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
