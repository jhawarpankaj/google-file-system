package edu.utd.aos.gfs.servers.meta.thread;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.servers.meta.MetaImpl;
import edu.utd.aos.gfs.utils.Helper;

public class MetaChunkStateObserver extends Thread {
	MetaImpl mimpl;

	public MetaChunkStateObserver(MetaImpl mimpl) {
		this.mimpl = mimpl;
	}

	@Override
	public void run() {
		Logger.info("Chunk State Observer Thread Started");
		while (true) {
			try {
				Helper.sleepForSec(5);
			} catch (GFSException e) {
				e.printStackTrace();
			}
			Timestamp current = new Timestamp(new Date().getTime());
			HashMap<String, Timestamp> chunkTimes = mimpl.getChunkTimes();
			for (Map.Entry<String, Timestamp> entry : chunkTimes.entrySet()) {
				Timestamp chunkTime = entry.getValue();
				String chunkserver = entry.getKey();
				int diff = Helper.getTimeDifference(chunkTime, current);
				if (diff > 15) {
					if (!mimpl.getChunkLiveness().containsKey(chunkserver)) {
						mimpl.updateChunkLiveness(chunkserver, GFSReferences.DEAD);
						Logger.info("Marked Chunkserver:" + chunkserver + " as DEAD");
					}
				}
			}

		}
	}
}
