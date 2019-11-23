package edu.utd.aos.gfs.dto;

import lombok.Data;

/**
 * @author pankaj
 * 
 * To store metadata server details.
 */
@Data
public class MetaServer {
	
	/**
	 * Name of the server. 
	 */
	String name;
	
	/**
	 * Port of the server.
	 */
	int port;
	
	/**
	 * Path of the root directory.
	 */
	String rootDir;

}
