package edu.utd.aos.mutex.dto;

import lombok.Data;

/**
 * Details of the client machines.
 * 
 * @author pankaj
 */
@Data
public class ClientDetails {
	
	/**
	 * Name of the client machine. 
	 */
	String name;
	
	/**
	 * Id of the client machine.
	 */
	int id;
}
