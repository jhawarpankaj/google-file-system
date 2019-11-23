package edu.utd.aos.gfs.servers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.utils.LocalHost;
import edu.utd.aos.gfs.utils.Sockets;

public class Client {
	public static void start() throws GFSException, IOException {
		Logger.info("Starting client server sockets for all external requests.");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(LocalHost.getPort());
			while(true) {
				Socket receiverSocket = null;
				receiverSocket = serverSocket.accept();
				DataInputStream dis = new DataInputStream(receiverSocket.getInputStream());
				DataOutputStream dos = new DataOutputStream(receiverSocket.getOutputStream());
	            Thread t = new ClientListener(receiverSocket, dis, dos);
	            t.start();
			}
		}catch(Exception e) {
			serverSocket.close();
			throw new GFSException("Error while receiving message: " + e);
		}		
	}

	public static void openCommandLineSocket() throws IOException {
		while(true) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));	       
	        String input = reader.readLine();
	        
	        // @Amtul: If you reached here, take a back seat, sip some water, and
	        // may be laugh a bit. It's a funny hack, I know, but it works :)
	        // All messages from the command line are being sent back to the same server,
	        // So that we can handle all input message at one place.
	        
	        // and Don't forget to delete these comments!!
	        
	        Sockets.sendMessage(LocalHost.getName(), LocalHost.getPort(), input);
		}
		
	}
}
