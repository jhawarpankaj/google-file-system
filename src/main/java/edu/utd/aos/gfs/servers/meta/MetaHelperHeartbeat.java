package edu.utd.aos.gfs.servers.meta;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.tinylog.Logger;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class MetaHelperHeartbeat {

	// size,version, ( comma separated server names)
	public static Table<String, String, List<String>> metaMap = HashBasedTable.create();

	public static void handleChunkHeartBeat(String server, Timestamp currentTime, MetaImpl mimpl, String received,
			ReentrantLock lock) {
		boolean outcome = isTimeDiffValid(server, currentTime, mimpl);
		if (outcome) {
			updateMetaMap(received, server, lock);
			if (isChunkDead(server, mimpl)) {
				Logger.info("Outcome is true: Chunk has been marked DEAD earlier, comparing the MetaMap:" + server);
				String compResult = compareMetaMap(server, received, lock, mimpl);
				Logger.info("RECOVERY delta:" + compResult);
				if (compResult != null && !compResult.isEmpty()) {
					Logger.info("Sending RECOVERY message");
					sendRecoverMessage(server, compResult);
				} else {
					Logger.info("Chunk updated after RECOVERY, removing it as DEAD");
					mimpl.removeDeadChunk(server);
				}
			}
		} else {
			Logger.info("Time>15sec, invalid interval. Chunk should have been marked DEAD earlier");
			if (isChunkDead(server, mimpl)) {
				Logger.info("Outcome is false: Chunk has been marked DEAD earlier, comparing the MetaMap");
				String compResult = compareMetaMap(server, received, lock, mimpl);
				Logger.info("RECOVERY delta:" + compResult);
				if (compResult != null && !compResult.isEmpty()) {
					Logger.info("Sending RECOVERY message");
					sendRecoverMessage(server, compResult);
				} else {
					Logger.info("Chunk missed nothing while it went down, no RECOVERY needed");
				}
			}
		}
	}

	public static void iterateHeartBeat(String server, JsonObject heartbeatJson) {
		for (Entry<String, JsonElement> entry : heartbeatJson.entrySet()) {
			String filename = entry.getKey();
			JsonObject jobj = entry.getValue().getAsJsonObject();
			for (Entry<String, JsonElement> entry2 : jobj.entrySet()) {
				String chunkname = entry2.getKey();
				JsonArray jarr = entry2.getValue().getAsJsonArray();
				String version = jarr.get(0).getAsString();
				String size = jarr.get(1).getAsString();
			}
		}
	}

	/**
	 * Compare and update the heart beat.
	 * 
	 * @param server
	 * @param heartbeatJson Input heartbeat in Json format.
	 */
	private static void updateHeartBeat(String server, JsonObject heartbeatJson) {
		// Logger.debug("Received heartbeat: " + heartbeatJson.toString() + ", from
		// server: " + server);
		// Logger.debug("Current meta map: " + metaMap);
		for (Map.Entry<String, JsonElement> jObj : heartbeatJson.entrySet()) {
			String fileName = jObj.getKey();
			JsonObject chunkjObj = jObj.getValue().getAsJsonObject();
			for (Map.Entry<String, JsonElement> allChunks : chunkjObj.entrySet()) {
				String chunkName = allChunks.getKey();
				JsonArray chunkSizeAndVersion = allChunks.getValue().getAsJsonArray();
				List<String> temp = getListFromJsonArray(chunkSizeAndVersion);
				// return [size, version]
				if (!metaMap.containsRow(fileName) || !metaMap.contains(fileName, chunkName)) {
					temp.add(server + ",");
					metaMap.put(fileName, chunkName, temp);
				} else {
					List<String> oldSizeAndVersionDetails = metaMap.get(fileName, chunkName);
					long oldSize = Long.parseLong(oldSizeAndVersionDetails.get(0));
					int oldVersion = Integer.parseInt(oldSizeAndVersionDetails.get(1));
					long newSize = Long.parseLong(temp.get(0));
					int newVersion = Integer.parseInt(temp.get(1));
					String serverCommaSeparated = oldSizeAndVersionDetails.get(2);
					String[] serverList = serverCommaSeparated.split(",");
					boolean flag = false;
					for (int i = 0; i < serverList.length; i++) {
						if (serverList[i].equalsIgnoreCase(server)) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						serverCommaSeparated = serverCommaSeparated + server + ",";
					}
					if (oldSize == newSize && oldVersion == newVersion) {
						if (!flag) {
							oldSizeAndVersionDetails.set(2, serverCommaSeparated);
							metaMap.put(fileName, chunkName, oldSizeAndVersionDetails);
						}
					} else if (newVersion >= oldVersion && newSize >= oldSize) {
						oldSizeAndVersionDetails.set(0, String.valueOf(newSize));
						oldSizeAndVersionDetails.set(1, String.valueOf(newVersion));
						if (!flag) {
							oldSizeAndVersionDetails.set(2, serverCommaSeparated);
						}
						metaMap.put(fileName, chunkName, oldSizeAndVersionDetails);
					}
				}
			}
		}
		// Logger.debug("Updated meta map: " + metaMap);
	}

	private static List<String> getListFromJsonArray(JsonArray chunkSizeAndVersion) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < chunkSizeAndVersion.size(); i++) {
			result.add(chunkSizeAndVersion.get(i).getAsString());
		}
		return result;
	}

	private static void updateMetaMap(String received, String server, ReentrantLock lock) {
		lock.lock();
		String message = Helper.getMessage(received);
		JsonObject heartbeatJson = Helper.getParsedHeartBeat(message);
		updateHeartBeat(server, heartbeatJson);
		lock.unlock();
	}

	private static boolean isTimeDiffValid(String server, Timestamp currentTime, MetaImpl mimpl) {
		if (mimpl.getChunkTimes().containsKey(server)) {
			Timestamp prevTime = mimpl.getChunkTimes().get(server);
			int diff = Helper.getTimeDifference(prevTime, currentTime);
			mimpl.updateChunkTimes(server, currentTime);
			if (diff <= 15) {
				return true;
			} else
				return false;
		} else {
			mimpl.updateChunkTimes(server, currentTime);
			return true;
		}
	}

	private static boolean isChunkDead(String server, MetaImpl mimpl) {
		if (mimpl.getChunkLiveness().containsKey(server)) {
			if (mimpl.getChunkLiveness().get(server).equalsIgnoreCase(GFSReferences.DEAD))
				return true;
			else
				return false;
		}
		return false;
	}

	private static String compareMetaMap(String server, String received, ReentrantLock lock, MetaImpl mimpl) {
		lock.lock();
		Logger.info("In Compare-MetaMap: Heartbeat from server:" + server + " is " + received);
		String message = Helper.getMessage(received);
		JsonObject heartbeatJson = Helper.getParsedHeartBeat(message);
		HashMap<String, HashMap<String, String>> chunkDelta = new HashMap<String, HashMap<String, String>>();
		for (Map.Entry<String, JsonElement> jObj : heartbeatJson.entrySet()) {
			String fileName = jObj.getKey();
			Logger.info("Filename:" + fileName);
			JsonObject chunkjObj = jObj.getValue().getAsJsonObject();
			for (Map.Entry<String, JsonElement> allChunks : chunkjObj.entrySet()) {
				String chunkName = allChunks.getKey();
				Logger.info("chunkName:" + chunkName);
				JsonArray chunkSizeAndVersion = allChunks.getValue().getAsJsonArray();
				String s_size = chunkSizeAndVersion.get(0).getAsString();
				String s_version = chunkSizeAndVersion.get(1).getAsString();
				if (metaMap.contains(fileName, chunkName)) {
					List<String> chunkdetails = metaMap.get(fileName, chunkName);
					String c_size = chunkdetails.get(0);
					String c_version = chunkdetails.get(1);
					if (!c_size.equalsIgnoreCase(s_size) || !c_version.equalsIgnoreCase(s_version)) {
						Logger.info("Version Mismatch for chunk&file:" + chunkName + "-" + fileName + " at server:"
								+ server);
						Logger.info("Updating the delta map for recovery");
						updateChunkDeltaMap(chunkDelta, fileName, chunkName, chunkdetails.get(2), server, mimpl);
					}
				}
			}
		}
		lock.unlock();
		String recoveryMessage = "";
		if (chunkDelta.size() == 0) {
			Logger.info("ChunkDelta is nill, nothing to update");
		} else {
			Logger.info("Finished preparing delta map, converting to recovery json string");
			recoveryMessage = getJsonStringForRecovery(chunkDelta);
		}
		return recoveryMessage;
	}

	private static void updateChunkDeltaMap(HashMap<String, HashMap<String, String>> chunkDelta, String filename,
			String chunkname, String servers, String serverInRecovery, MetaImpl mimpl) {
		Logger.info("in update delta map");
		if (chunkDelta.containsKey(filename)) {
			HashMap<String, String> chunkAndServer = chunkDelta.get(filename);
			String server = chooseServerForRecover(servers, serverInRecovery, mimpl, chunkname, filename);
			chunkAndServer.put(chunkname, server);
			chunkDelta.put(filename, chunkAndServer);

		} else {
			HashMap<String, String> chunkAndServer = new HashMap<String, String>();
			String server = chooseServerForRecover(servers, serverInRecovery, mimpl, chunkname, filename);
			chunkAndServer.put(chunkname, server);
			chunkDelta.put(filename, chunkAndServer);
		}

	}

	private static String chooseServerForRecover(String servers, String serverInRecovery, MetaImpl mimpl,
			String chunknum, String filename) {
		String server = "";
		String serverlist[] = servers.split(",");
		for (String s : serverlist) {
			if (!s.equalsIgnoreCase(serverInRecovery) && !mimpl.getChunkLiveness().containsKey(s)) {
				Logger.info("Found a suitable server to recover from for " + chunknum + "-" + filename);
				server = s;
				break;
			}
		}
		if (server.isEmpty())
			Logger.info("All replicas for " + chunknum + "-" + filename + " are down. Not sending any server");
		return server;

	}

	private static String getJsonStringForRecovery(HashMap<String, HashMap<String, String>> deltaMap) {
		Gson gsonBuilder = new GsonBuilder().create();
		String jsonFromJavaMap = gsonBuilder.toJson(deltaMap);
		Logger.info("Converted string:" + jsonFromJavaMap);
		return jsonFromJavaMap;
	}

	private static void sendRecoverMessage(String server, String message) {
		String forward = GFSReferences.RECOVER + GFSReferences.SEND_SEPARATOR;
		forward += message;
		Sockets.sendMessage(server, Nodes.getPortByHostName(server), forward);
	}

}
