package edu.utd.aos.gfs.dto;

import java.util.List;

import lombok.Data;

/**
 * @author pankaj
 * 
 * Details for the chunk server.
 */
@Data
public class ChunkServerDetails {
	
	/**
	 * Root dir at the chunk server.
	 */
	private String rootDir;
	
	/**
	 * Chunk server nodes details.
	 */
	private List<ChunkServer> nodes;

}
