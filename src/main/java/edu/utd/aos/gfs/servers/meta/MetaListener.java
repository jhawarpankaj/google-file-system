package edu.utd.aos.gfs.servers.meta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;

public class MetaListener extends Thread {
	final Socket worker;
	final DataInputStream dis;
	final DataOutputStream dos;
	MetaImpl mimpl;
	String message;
	public static final ReentrantLock lock = new ReentrantLock();

	public MetaListener(Socket worker, DataInputStream dis, DataOutputStream dos, MetaImpl mimpl, String message) {
		this.worker = worker;
		this.dis = dis;
		this.dos = dos;
		this.mimpl = mimpl;
		this.message = message;
	}

	@Override
	public void run() {
		try {
			String received = message;
			String server = this.worker.getInetAddress().getHostName();
			String command = Helper.getCommand(received);
			switch (command) {

			case GFSReferences.HEARTBEAT:
				String message = Helper.getMessage(received);
				// Logger.info("Received heartbeat, not doing anything for now");
				// JsonObject heartbeatJson = Helper.getParsedHeartBeat(message);
				// Logger.debug("Parsed heart beat message: " + heartbeatJson); // TODO
				// Helper.iterateHeartBeat(server, heartbeatJson);
				break;

			case GFSReferences.CREATE_ACK:
				MetaHelperCreate.handleCreateAck(server, mimpl);
				break;

			default:
				throw new GFSException("Unidentified input: " + command + " received on META server!!");
			}

		} catch (Exception e) {
			Logger.error("Error while performing client request: " + e);
		}
	}
}
