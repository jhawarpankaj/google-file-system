package edu.utd.aos.gfs.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class ClientListener extends Thread {
	final Socket worker;
	final ServerSocket owner;
	final DataInputStream dis;
	final DataOutputStream dos;
	ClientImpl cimpl;
	public static final ReentrantLock lock = new ReentrantLock();

	public ClientListener(Socket worker, DataInputStream dis, DataOutputStream dos, ServerSocket owner,
			ClientImpl cimpl) {
		this.worker = worker;
		this.dis = dis;
		this.dos = dos;
		this.owner = owner;
		this.cimpl = cimpl;
	}

	@Override
	public void run() {
		try {
			String received = dis.readUTF();
			String sender = this.worker.getInetAddress().getHostName();
			String owner = this.owner.getInetAddress().getLocalHost().getHostName();
			if (owner.equalsIgnoreCase(sender)) {
				Logger.info("Forwarding to Meta Server:" + received);
				Sockets.sendMessage(Nodes.metaServerName(), Nodes.metaServerPort(), received);
			} else {
				String command = Helper.getCommand(received);
				switch (command) {
				case GFSReferences.READ:
					Logger.info("Received READ from Meta");
					ClientHelper.forwardReadToChunk(received, cimpl);
					break;
				case GFSReferences.READ_CONTENT:
					ClientHelper.handleReadResponse(received, cimpl);
					break;
				case GFSReferences.APPEND:
					Logger.info("Received APPEND from Meta");
					ClientHelper.forwardAppendToChunk(received, cimpl);
					break;
				case GFSReferences.COMMIT_ACK:
					Logger.info("Received COMMIT_ACK from Chunks");
					ClientHelper.handleCommitAck(received, sender, cimpl);
					break;
				case GFSReferences.READY_TO_APPEND:
					ClientHelper.handleReadyToAppendResponse(received, sender, cimpl);
					break;
				case GFSReferences.CREATE_SUC:
					ClientHelper.handleCreateResponse(received);
					break;

				default:
					throw new GFSException("Unidentified input: " + command + " received on CLIENT server!!");
				}
			}
		} catch (Exception e) {
			Logger.error("Error while performing client request: " + e);
		}
	}
}
