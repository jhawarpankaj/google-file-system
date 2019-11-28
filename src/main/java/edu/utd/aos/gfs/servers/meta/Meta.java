package edu.utd.aos.gfs.servers.meta;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.utils.Helper;
import edu.utd.aos.gfs.utils.LocalHost;

public class Meta {

	public static void start() throws GFSException, IOException {
		Logger.info("Starting meta server sockets for all external requests.");
		ServerSocket serverSocket = null;
		MetaImpl mimpl = new MetaImpl();
		Thread queueThread = new MetaQueueListener(mimpl);
		queueThread.start();
		try {
			serverSocket = new ServerSocket(LocalHost.getPort());

			while (true) {
				Socket receiverSocket = null;
				receiverSocket = serverSocket.accept();
				DataInputStream dis = new DataInputStream(receiverSocket.getInputStream());
				DataOutputStream dos = new DataOutputStream(receiverSocket.getOutputStream());
				String message = dis.readUTF();
				if (Helper.isQueueableMessage(message)) {
					mimpl.addToDeferredQueue(message, receiverSocket.getInetAddress().getHostName());
				} else {
					Thread t = new MetaListener(receiverSocket, dis, dos, mimpl, message);
					t.start();
				}
			}
		} catch (Exception e) {			
			serverSocket.close();
			throw new GFSException("Error while receiving message:" + e);
		}
	}
}
