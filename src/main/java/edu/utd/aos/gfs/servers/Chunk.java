package edu.utd.aos.gfs.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import org.tinylog.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;
import edu.utd.aos.gfs.utils.LocalHost;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class Chunk {
	
	/**
	 * Infinite thread for all incoming requests.
	 * @throws GFSException While creating new threads.
	 * @throws IOException While closing sockets.
	 */
	public static void start() throws GFSException, IOException {
		Logger.info("Starting chunk server sockets for all external requests.");
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(LocalHost.getPort());
			Logger.info("Created socket!!");
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

	/**
	 * Share feelings with the Meta server every 5 seconds.
	 * @throws GFSException 
	 */
	public static void sendHeartBeats() throws GFSException {
		while(true) {			
			String message = GFSReferences.HEARTBEAT + GFSReferences.SEND_SEPARATOR + getChunkDetails();
			Logger.info("HEARTBEAT message to META server: " + message);
			Sockets.sendMessage(Nodes.metaServerName(), Nodes.metaServerPort(), message);
			Helper.sleepForSec(5);
		}
	}

	private static String getChunkDetails() {
		String rootDir = LocalHost.getUniqueChunkPath();
		JsonObject jObj = new JsonObject();
		
		// To get a list of all dir.
		ArrayList<File> allFiles = new ArrayList<File>(
				Arrays.asList(new File(rootDir).listFiles(File::isDirectory))
			);
				
		// Iterate over all dir and get each chunk's name.
		for(File file: allFiles) {
			String name = file.getName();
			Logger.info("File name: " + name);
			JsonArray jArr = new JsonArray();
			ArrayList<File> allChunks = new ArrayList<File>(
				    Arrays.asList(new File(rootDir + name).listFiles(File::isFile))
				);
			for(File chunk: allChunks) {
				jArr.add(chunk.getName());
			}
			jObj.add(name, jArr);
		}
		return jObj.toString();		
	}
}
