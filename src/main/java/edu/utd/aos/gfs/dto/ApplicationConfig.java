package edu.utd.aos.gfs.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Config class for reading all details.
 * 
 * @author pankaj
 */
@Data
public class ApplicationConfig {
	
	/**
	 * All Meta, Client and Chunk server details.
	 */
	private NodeDetails nodeDetails;
}
