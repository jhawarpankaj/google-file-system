package edu.utd.aos.gfs.servers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
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
	public static void sendInfiniteHeartBeats() throws GFSException {
		while(true) {			
			sendAHeartBeat();
			Helper.sleepForSec(5);
		}
	}
	
	/**
	 * Send a single heart beat message.
	 * @throws GFSException Error while forming the heartbeat message. File not found.
	 */
	public static void sendAHeartBeat() throws GFSException {
		String message;
		try {
			message = GFSReferences.HEARTBEAT + GFSReferences.SEND_SEPARATOR + getChunkDetails();
		} catch (IOException e) {
			throw new GFSException("Error while preparing heartbeat json." + e);
		}
		Logger.info("HEARTBEAT message to META server: " + message);
		Sockets.sendMessage(Nodes.metaServerName(), Nodes.metaServerPort(), message);
	}

	/**
	 * Prepare heart beat message.
	 * @return String heartbeat message.
	 * @throws IOException File not founds.
	 */
	private static String getChunkDetails() throws IOException {
		String rootDir = LocalHost.getUniqueChunkPath();
		JsonObject jObj = new JsonObject();
		
		// To get a list of all dir.
		ArrayList<File> allFiles = new ArrayList<File>(
				Arrays.asList(new File(rootDir).listFiles(File::isDirectory))
			);
				
		// Iterate over all dir and get each chunk's name.
		for(File file: allFiles) {
			String fileName = file.getName();
			Logger.info("File name: " + fileName);
			
			ArrayList<File> allChunks = new ArrayList<File>(
				    Arrays.asList(new File(rootDir + fileName).listFiles(File::isFile))
				);
			JsonObject chunkjObj = new JsonObject();
			for(File chunk: allChunks) {
				JsonArray jArr = new JsonArray();
				String chunkName = chunk.getName();
				if(chunkName.endsWith(".version")) continue;
				String size = String.valueOf(chunk.length());
				File versionFile = new File(rootDir + fileName + "/" + chunkName + ".version");
				BufferedReader buff = new BufferedReader(new FileReader(versionFile));
				String version = buff.readLine();
				buff.close();
				jArr.add(size);
				jArr.add(version);
				chunkjObj.add(chunkName, jArr);
			}
			jObj.add(fileName, chunkjObj);
		}
		return jObj.toString();		
	}
}
