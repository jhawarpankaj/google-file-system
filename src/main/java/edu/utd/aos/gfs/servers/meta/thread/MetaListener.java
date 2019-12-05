package edu.utd.aos.gfs.servers.meta.thread;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.servers.meta.MetaHelperAppend;
import edu.utd.aos.gfs.servers.meta.MetaHelperCreate;
import edu.utd.aos.gfs.servers.meta.MetaHelperHeartbeat;
import edu.utd.aos.gfs.servers.meta.MetaImpl;
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
			Timestamp current = new Timestamp(new Date().getTime());
			switch (command) {
				case GFSReferences.TENDERFEELINGS:
					System.out.println(MetaHelperHeartbeat.metaMap);
					break;
				case GFSReferences.HEARTBEAT:
					MetaHelperHeartbeat.handleChunkHeartBeat(server, current, mimpl, received, lock);
					break;
				case GFSReferences.CREATE_ACK:
					MetaHelperCreate.handleCreateAck(server, mimpl);
					break;
				case GFSReferences.APPEND_ACK_META:
					MetaHelperAppend.handleAppendAck(server, mimpl);
					break;
				case GFSReferences.PAD_NULL_ACK:
					MetaHelperAppend.handlePadAck(server, mimpl);
					break;
				case GFSReferences.CREATE_CHUNK_ACK:
					MetaHelperAppend.handleChunkCreateAck(server, mimpl);
					break;
				default:
					throw new GFSException("Unidentified input: " + command + " received on META server!!");
			}
		} catch (Exception e) {
			Logger.error("Error while performing client request: " + e);
			e.printStackTrace();
		}
	}
}