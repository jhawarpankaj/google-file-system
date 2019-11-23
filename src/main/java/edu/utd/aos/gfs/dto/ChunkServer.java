package edu.utd.aos.gfs.dto;

import lombok.Data;

/**
 * Details of the chunk servers.
 * 
 * @author pankaj
 */
@Data
public class ChunkServer {
	
	/**
	 * Name of the chunk server. 
	 */
	String name;
	
	/**
	 * Port of the chunk server.
	 */
	int port;
}
