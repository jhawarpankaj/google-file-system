package edu.utd.aos.gfs.utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.servers.Chunk;
import edu.utd.aos.gfs.servers.Client;
import edu.utd.aos.gfs.servers.meta.Meta;
import edu.utd.aos.gfs.utils.LocalHost.Type;

public class Sockets {

	public static void intialize() throws GFSException, IOException {
		Type type = LocalHost.getType();
		switch (type) {

		case META:
			
			new Thread(() -> {
				try {
					Meta.openCommandLineSocket();
				} catch (IOException e) {
					Logger.error("Error while initializing command line socket: " + e);
				}
			}).start();
			
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
			
			System.out.println("\nHi! I am a baby Google File System. Please handle me with care.");
			System.out.println("For now, my creators has just trained me to act on below commands: \n");
			System.out.println("1. CREATE||filename");
			System.out.println("2. APPEND||filename||bytes (Mom says no more than 1048 at a time)");
			System.out.println("3. READ||filename||offset\n");
			System.out.println("Play time: ");

			// Infinite input listener socket.
			Client.start();
			break;

		case CHUNK:

			// Infinite input listener socket.
			new Thread(() -> {
				try {
					Chunk.start();
				} catch (IOException | GFSException e) {
					Logger.error("Error while initializing command line socket: " + e);
				}
			}).start();

			// Adding this sleep temporarily to manually create the files on the
			// chunk server to test HEARTBEAT message until Amtul implements the create
			// command.

			Helper.sleepForSec(10);

			// Infinite HeartBeat. True Love!! Or perhaps closer to death o.O
			Chunk.sendInfiniteHeartBeats();
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
			if (message != null && message.length() < 200)
				Logger.info("Sent " + message + " message to node: " + name);
		} catch (Exception e) {
			Logger.error("Error while sending " + message + " message to node: " + name);
			Logger.debug(e.getMessage());
		}
	}
}
