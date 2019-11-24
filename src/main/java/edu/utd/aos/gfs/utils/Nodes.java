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
 * Get all nodes related stuff here.
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

}
