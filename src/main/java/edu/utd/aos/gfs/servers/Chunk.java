package edu.utd.aos.gfs.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.utils.LocalHost;

public class Chunk {
	public static void start() throws GFSException, IOException {
		Logger.info("Starting chunk server sockets for all external requests.");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(LocalHost.getPort());
			while(true) {
				Socket receiverSocket = null;
				receiverSocket = serverSocket.accept();
				DataInputStream dis = new DataInputStream(receiverSocket.getInputStream());
				DataOutputStream dos = new DataOutputStream(receiverSocket.getOutputStream());
	            Thread t = new ChunkListener(receiverSocket, dis, dos);
	            t.start();
			}
		}catch(Exception e) {
			serverSocket.close();
			throw new GFSException("Error while receiving message: " + e);
		}		
	}
}
