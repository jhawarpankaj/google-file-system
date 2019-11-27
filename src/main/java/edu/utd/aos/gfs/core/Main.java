package edu.utd.aos.gfs.core;

import java.io.IOException;
import java.util.List;

import org.tinylog.Logger;

import edu.utd.aos.gfs.dto.ApplicationConfig;
import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.dto.ClientServer;
import edu.utd.aos.gfs.dto.MetaServer;
import edu.utd.aos.gfs.dto.NodeDetails;
import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSConfigHolder;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.LocalHost;
import edu.utd.aos.gfs.utils.Sockets;

public class Main {

	public static void main(final String[] args) {
		Logger.info("Hello World");
		try {
			initialize();
		} catch (final Exception e) {
			Logger.error("Exiting, error: " + e);
			e.printStackTrace();
			System.exit(GFSReferences.CONST_CODE_ERROR);
		}

		ApplicationConfig applicationConfig = GFSConfigHolder.getApplicationConfig();
		NodeDetails nodeDetails = applicationConfig.getNodeDetails();
		MetaServer metaServer = nodeDetails.getMetaServer();
		List<ChunkServer> chunkServer = nodeDetails.getChunkServerDetails().getNodes();
		List<ClientServer> clientServer = nodeDetails.getClientServer();
	}

	private static void initialize() throws GFSException {
		GFSConfigHolder.initialize();
		LocalHost.initializeNodeDetails();
		LocalHost.initializeNodeDir();
		try {
			Sockets.intialize();
		} catch (IOException e) {
			throw new GFSException("Error while initializing sockets: " + e);
		}
	}

	private static void setup() {
	}
}