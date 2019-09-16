package edu.utd.aos.mutex.dto;

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
	 * Server Details.
	 */
	List<ServerDetails> serverDetails;
	
	/**
	 * Client Details.
	 */
	List<ClientDetails> clientDetails;
}
