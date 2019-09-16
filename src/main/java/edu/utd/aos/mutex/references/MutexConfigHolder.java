package edu.utd.aos.mutex.references;

import edu.utd.aos.mutex.dto.ApplicationConfig;
import edu.utd.aos.mutex.exception.MutexException;

import java.io.BufferedReader;
import java.io.FileReader;

import org.tinylog.Logger;

import com.google.gson.Gson;

/**
 * This class initializes the configuration and make it available across the project.
 * 
 * @author pankaj
 */
public class MutexConfigHolder {
	
	/**
	 * Reading configuration details.
	 */
	private static ApplicationConfig applicationConfig;
	
	/**
	 * Initialize all config files.
	 * 
	 * @throws MutexException
	 */
	public static void initialize() throws MutexException{
		Logger.info("Initializing application config file.");
		final String configFile = System.getProperty(MutexReferences.KEY_MUTEX_CONFIG);
		try {
			BufferedReader br = new BufferedReader(new FileReader(configFile));
			final Gson gson = new Gson();
			applicationConfig = gson.fromJson(br, ApplicationConfig.class);
		}catch(Exception e) {
			throw new MutexException("Error while reading configuration file." + e);
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
	private MutexConfigHolder() {
		
	}
}
