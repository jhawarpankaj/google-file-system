package edu.utd.aos.gfs.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.servers.Chunk;
import edu.utd.aos.gfs.servers.Client;
import edu.utd.aos.gfs.servers.Meta;
import edu.utd.aos.gfs.utils.LocalHost.Type;

public class Sockets {

	public static void intialize() throws GFSException, IOException {
		Type type = LocalHost.getType();
		switch(type) {
		
			case META:
				// Infinite input listener socket.
				Meta.start();
				break;
				
			case CLIENT:
				// Infinite command line socket.
				new Thread(() -> {
					try {
						Client.openCommandLineSocket();
					} catch (IOException e) {
						Logger.error("Error while initializing command line socket: " + e);
					}
				}).start();
				
				// Infinite input listener socket.
				Client.start();
				break;
				
			case CHUNK:
				
				// Infinite input listener socket.
				new Thread(() -> {
					try {
						Chunk.start();
					}catch(IOException | GFSException e) {
						Logger.error("Error while initializing command line socket: " + e);
					}					
				}).start();
				
				// Adding this sleep temporarily to manually create the files on the 
				// chunk server to test HEARTBEAT message until Amtul implements the create command.
				
				Helper.sleepForSec(10);				
				
				// Infinite HeartBeat. True Love!!
				Chunk.sendHeartBeats();
				break;
			default:
				throw new GFSException("Unidentified node type!");
		}
	}
	
	public static void sendMessage(String name, int port, String message) {
		try {
			Socket socket = null;
			DataOutputStream out = null;
			socket = new Socket(name, port);
			out = new DataOutputStream(socket.getOutputStream());
			out.writeUTF(message);
			socket.close();
			Logger.info("Sent " + message + " message to node: " + name);
		}catch(Exception e) {
			Logger.error("Error while sending " + message + " message to node: " + name);
		}
	}
}
