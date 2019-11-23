package edu.utd.aos.gfs.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.tinylog.Logger;

import edu.utd.aos.gfs.dto.ApplicationConfig;
import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.dto.ClientServer;
import edu.utd.aos.gfs.dto.MetaServer;
import edu.utd.aos.gfs.dto.NodeDetails;
import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSConfigHolder;

/**
 * @author pankaj
 * 
 * Initialize all localhost details.
 */
public class LocalHost {
	
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
	 * Available types.
	 */
	public enum Type{
		META, CLIENT, CHUNK
	};

	public static void initialize() throws GFSException {
		Logger.info("Initializing localhost details.");
		
		InetAddress ip;
		ApplicationConfig applicationConfig = GFSConfigHolder.getApplicationConfig();
		NodeDetails nodeDetails = applicationConfig.getNodeDetails();
		MetaServer metaServer = nodeDetails.getMetaServer();
		List<ChunkServer> chunkServer = nodeDetails.getChunkServer();
		List<ClientServer> clientServer = nodeDetails.getClientServer();
		
		try {
			ip = InetAddress.getLocalHost();
			name = ip.getHostName().toLowerCase();
		} catch (UnknownHostException e) {
			throw new GFSException("Error while fetching the hostname: " + e);
		}
		
		if(name.equalsIgnoreCase(metaServer.getName())){
			type = Type.META;
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
				port = server.getPort();
				return;
			}
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
	 * Private constructor for utility classes.
	 */
	private LocalHost() {
		
	}

}
