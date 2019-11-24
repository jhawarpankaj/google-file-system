package edu.utd.aos.gfs.references;

/**
 * Constants and references used across project.
 * 
 * @author pankaj
 *
 */
public class GFSReferences {

	/**
	 * System Property that provides URI for the config.
	 */
	public static final String KEY_GFS_CONFIG = "gfs.config";
	
	/**
	 * Exit code for errors and exception.
	 */
	public static final int CONST_CODE_ERROR = 1;
	
	/**
	 * Heart do beat!!
	 */
	public static final String HEARTBEAT = "HEARTBEAT";
	
	/**
	 * Command used by Client to create a new file.
	 */
	public static final String CREATE = "CREATE";
	
	/**
	 * Command used by client to read a file.
	 */
	public static final String READ = "READ";
	
	/**
	 * Command used by client to append to a file.
	 */
	public static final String APPEND = "APPEND";
	
	/**
	 * Separator used while splitting. Backslash for Regex Escape.
	 */
	public static final String REC_SEPARATOR = "\\|\\|";
	
	/**
	 * Separator used while sending.
	 */
	public static final String SEND_SEPARATOR = "||";

	
	
	/**
	 * Private constructor for utility class.
	 */
	private GFSReferences() {
		
	}
}
