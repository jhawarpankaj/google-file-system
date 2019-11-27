package edu.utd.aos.gfs.utils;

import java.util.List;

import edu.utd.aos.gfs.dto.ApplicationConfig;
import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.dto.ClientServer;
import edu.utd.aos.gfs.dto.MetaServer;
import edu.utd.aos.gfs.dto.NodeDetails;
import edu.utd.aos.gfs.references.GFSConfigHolder;

/**
 * @author pankaj
 * 
 *         Get all nodes related stuff here.
 */
public class Nodes {

	private static final ApplicationConfig applicationConfig = GFSConfigHolder.getApplicationConfig();
	private static final NodeDetails nodeDetails = applicationConfig.getNodeDetails();
	private static final MetaServer metaServer = nodeDetails.getMetaServer();

	public static String metaServerName() {
		return metaServer.getName();
	}

	public static int metaServerPort() {
		return metaServer.getPort();
	}

	public static String metaServerRootDir() {
		return metaServer.getRootDir();
	}

	public static List<ChunkServer> chunkServersList() {
		return nodeDetails.getChunkServerDetails().getNodes();
	}

	public static List<ClientServer> clientServersList() {
		return nodeDetails.getClientServer();
	}

	/**
	 * Get port name from a unique host name
	 * 
	 * @param hostname
	 * @return
	 */
	public static int getPortByHostName(String hostname) {
		int port = -1;

		if (metaServerName().equalsIgnoreCase(hostname))
			return metaServerPort();

		List<ClientServer> clients = clientServersList();
		for (ClientServer client : clients) {
			if (client.getName().equalsIgnoreCase(hostname)) {
				port = client.getPort();
				return port;
			}
		}
		List<ChunkServer> chunkServers = chunkServersList();
		for (ChunkServer chunkserver : chunkServers) {
			if (chunkserver.getName().equalsIgnoreCase(hostname)) {
				port = chunkserver.getPort();
				return port;
			}
		}
		return port;
	}

	/////// AMTUL 26NOV
	public static int getClientIDByHostName(String hostname) {
		int id = -1;

		List<ClientServer> clients = clientServersList();
		for (ClientServer client : clients) {
			if (client.getName().equalsIgnoreCase(hostname)) {
				id = client.getId();
				return id;
			}
		}
		return id;
	}
}
