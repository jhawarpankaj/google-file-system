package edu.utd.aos.gfs.dto;

import lombok.Data;

/**
 * Details of servers.
 * 
 * @author pankaj
 */
@Data
public class ClientServer {

	/**
	 * Name of the server.
	 */
	String name;

	/**
	 * Port of the server.
	 */
	int port;
	/**
	 * ID of the server
	 */
	int id;

}
