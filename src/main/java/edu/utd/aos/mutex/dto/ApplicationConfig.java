package edu.utd.aos.mutex.dto;
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
	 * All server and client details.
	 */
	private NodeDetails nodeDetails;
}
