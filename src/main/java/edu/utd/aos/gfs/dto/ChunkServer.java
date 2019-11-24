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
	 * Unique id for each chunk server. 
	 */
	int id;
	
	/**
	 * Name of the chunk server. 
	 */
	String name;
	
	/**
	 * Port of the chunk server.
	 */
	int port;
	
}
