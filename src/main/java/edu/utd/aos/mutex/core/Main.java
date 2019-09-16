package edu.utd.aos.mutex.core;
import java.util.List;

import org.tinylog.Logger;

import edu.utd.aos.mutex.dto.ApplicationConfig;
import edu.utd.aos.mutex.dto.NodeDetails;
import edu.utd.aos.mutex.dto.ServerDetails;
import edu.utd.aos.mutex.references.MutexConfigHolder;
import edu.utd.aos.mutex.references.MutexReferences;

public class Main {

	public static void main(final String[] args) {
		Logger.info("Hello World!");
		try {
			MutexConfigHolder.initialize();
		}catch(final Exception e) {
			System.exit(MutexReferences.CONST_CODE_ERROR);
		}
		
		ApplicationConfig applicationConfig = MutexConfigHolder.getApplicationConfig();
		NodeDetails nodeDetails = applicationConfig.getNodeDetails();
		List<ServerDetails> serverDetails = nodeDetails.getServerDetails();
		serverDetails.forEach(server -> {
			Logger.info("Name:" + server.getName());
			Logger.info("ID:" + server.getId());
		});
	}
}