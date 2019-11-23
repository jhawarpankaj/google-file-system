package edu.utd.aos.gfs.references;

import edu.utd.aos.gfs.dto.ApplicationConfig;
import edu.utd.aos.gfs.exception.GFSException;

import java.io.BufferedReader;
import java.io.FileReader;

import org.tinylog.Logger;

import com.google.gson.Gson;

/**
 * This class initializes the configuration and make it available across the project.
 * 
 * @author pankaj
 */
public class GFSConfigHolder {
	
	/**
	 * Reading configuration details.
	 */
	private static ApplicationConfig applicationConfig;
	
	/**
	 * Initialize all config files.
	 * 
	 * @throws GFSException
	 */
	public static void initialize() throws GFSException{
		Logger.info("Initializing application config file.");
		final String configFile = System.getProperty(GFSReferences.KEY_GFS_CONFIG);
		try {
			BufferedReader br = new BufferedReader(new FileReader(configFile));
			final Gson gson = new Gson();
			applicationConfig = gson.fromJson(br, ApplicationConfig.class);
		}catch(Exception e) {
			throw new GFSException("Error while reading configuration file." + e);
		}
		
	}
	
	/**
	 * @return Cached application Config.
	 */
	public static ApplicationConfig getApplicationConfig() {
		return applicationConfig;
	}
	
	/**
	 * Constructor for utility class.
	 */
	private GFSConfigHolder() {
		
	}
}
