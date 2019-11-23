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
				Meta.start();
				break;
			case CLIENT:
				new Thread(() -> {
					try {
						Client.openCommandLineSocket();
					} catch (IOException e) {
						Logger.error("Error while initializing command line socket: " + e);
					}
				}).start();
				Client.start();
				break;
			case CHUNK:
				Chunk.start();
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
