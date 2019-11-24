package edu.utd.aos.gfs.utils;

import java.io.File;
import java.net.InetAddress;
import java.net.SocketImpl;
import java.net.UnknownHostException;
import java.util.List;

import javax.tools.DocumentationTool.Location;

import org.tinylog.Logger;

import edu.utd.aos.gfs.dto.ApplicationConfig;
import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.dto.ChunkServerDetails;
import edu.utd.aos.gfs.dto.ClientServer;
import edu.utd.aos.gfs.dto.MetaServer;
import edu.utd.aos.gfs.dto.NodeDetails;
import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSConfigHolder;
import lombok.Data;

/**
 * @author pankaj
 * 
 * Initialize all localhost details.
 */
@Data
public class LocalHost {
	
	/**
	 * Unique id, used in case of Chunk servers.
	 */
	private static int id;
	
	/**
	 * Localhost name.
	 */
	private static String name;
	
	/**
	 * Local Port.
	 */
	private static int port;
	
	/**
	 * Type of localhost.
	 */
	private static Type type;
	
	/**
	 * Root directory, used in case of Chunk and meta server.
	 */
	private static String rootDir;
	
	/**
	 * Available types.
	 */
	public enum Type{
		META, CLIENT, CHUNK
	};

	public static void initializeNodeDetails() throws GFSException {
		Logger.info("Initializing localhost details.");
		
		InetAddress ip;
		ApplicationConfig applicationConfig = GFSConfigHolder.getApplicationConfig();
		NodeDetails nodeDetails = applicationConfig.getNodeDetails();
		MetaServer metaServer = nodeDetails.getMetaServer();
		ChunkServerDetails chunkServerDetails = nodeDetails.getChunkServerDetails();
		List<ChunkServer> chunkServer = chunkServerDetails.getNodes();
		List<ClientServer> clientServer = nodeDetails.getClientServer();
		
		try {
			ip = InetAddress.getLocalHost();
			name = ip.getHostName().toLowerCase();
		} catch (UnknownHostException e) {
			throw new GFSException("Error while fetching the hostname: " + e);
		}
		
		if(name.equalsIgnoreCase(metaServer.getName())){
			type = Type.META;
			rootDir = metaServer.getRootDir();
			port = metaServer.getPort();
			return;
		}		
		
		for(ClientServer server: clientServer) {
			if(name.equalsIgnoreCase(server.getName())) {
				type = Type.CLIENT;
				port = server.getPort();
				return;
			}
		}
		
		for(ChunkServer server: chunkServer) {
			if(name.equalsIgnoreCase(server.getName())) {
				type = Type.CHUNK;
				rootDir = chunkServerDetails.getRootDir();
				id = server.getId();				
				port = server.getPort();
				return;
			}
		}
	}
	
	/**
	 * Create all required directories. 
	 */
	public static void initializeNodeDir() {
		
		Type node = LocalHost.getType();
		File directory = null;
		switch(node) {
			case META:
				directory = new File(LocalHost.getRootDir());
				break;
			case CHUNK:
			    directory = new File(LocalHost.getUniqueChunkPath());			    
				break;
			case CLIENT:
				Logger.debug("No directories to be created for Client");
				return;
		}
		if(!directory.exists()){
	    	directory.mkdirs();
	    }
	}
	
	/**
	 * @return Type of the node.
	 */
	public static Type getType() {
		return type;
	}
	
	/**
	 * @return port of the host.
	 */
	public static int getPort() {
		return port;
	}
	
	/**
	 * @return name of the localhost.
	 */
	public static String getName() {
		return name;
	}
	
	/**
	 * @return Unique id used for Chunk servers.
	 */
	public static int getID() {
		return id;
	}
	
	/**
	 * @return Root directory used in case of meta and chunk servers.
	 */
	public static String getRootDir() {
		return rootDir;
	}
	
	/**
	 * @return Unique path where each chunk server stores their chunks.
	 */
	public static String getUniqueChunkPath() {
		return getRootDir() + getID() + "/";
	}
	
	/**
	 * Private constructor for utility classes.
	 */
	private LocalHost() {
		
	}

}
