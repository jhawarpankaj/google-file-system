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
	public static final ReentrantLock lock = new ReentrantLock();

	public ClientListener(Socket worker, DataInputStream dis, DataOutputStream dos, ServerSocket owner) {
		this.worker = worker;
		this.dis = dis;
		this.dos = dos;
		this.owner = owner;
	}

	@Override
	public void run() {
		try {
			String received = dis.readUTF();
			String sender = this.worker.getInetAddress().getHostName();
			String owner = this.owner.getInetAddress().getLocalHost().getHostName();
			if (owner.equalsIgnoreCase(sender)) {
				Logger.info("Forwarding the terminal input to Meta Server");
				Sockets.sendMessage(Nodes.metaServerName(), Nodes.metaServerPort(), received);
			} else {
				String command = Helper.getCommand(received);
				switch (command) {
				case GFSReferences.READ:
					Logger.info("Received READ details from Meta");
					ClientHelper.forwardReadToChunk(received);
					break;
				case GFSReferences.APPEND:
					break;
				case GFSReferences.CREATE_SUC:
					ClientHelper.handleCreateSuc(command);
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
