package edu.utd.aos.gfs.dto;

import java.util.List;

import lombok.Data;

/**
 * Details of all nodes.
 * 
 * @author pankaj
 */
@Data
public class NodeDetails {
	
	/**
	 * Metadata server details.
	 */
	MetaServer metaServer;
	
	/**
	 * Client Server Details.
	 */
	List<ClientServer> clientServer;
	
	/**
	 * Chunk Server Details.
	 */
	ChunkServerDetails chunkServerDetails;
}
