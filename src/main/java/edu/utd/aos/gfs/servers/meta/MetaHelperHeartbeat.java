package edu.utd.aos.gfs.servers.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.tinylog.Logger;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.aos.gfs.utils.Helper;

public class MetaHelperHeartbeat {

	// size,version, (,separated server names)
	public static Table<String, String, List<String>> metaMap = HashBasedTable.create();

	public static void main(String[] args) {
		// String json = "{\"file1\":{\"chunk1\":[\"0\",\"0\"]}}";
		String json = "{\"file1\":{\"chunk1\":{[1, 223]},\"chunk2\":{[0, 123]}},\"file2\":{\"chunk1\":{[3, 2232]},\"chunk3\":{[3, 2232]}}}";
		JsonObject message = Helper.getParsedHeartBeat(json);
		System.out.println(message);
		// iterateHeartBeat("dc04.utdallas.edu", message);

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
	public static void updateHeartBeat(String server, JsonObject heartbeatJson) {
		Logger.debug("Received heartbeat: " + heartbeatJson.toString() + ", from server: " + server);
		Logger.debug("Current meta map: " + metaMap);
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
		Logger.debug("Updated meta map: " + metaMap);
	}

	private static List<String> getListFromJsonArray(JsonArray chunkSizeAndVersion) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < chunkSizeAndVersion.size(); i++) {
			result.add(chunkSizeAndVersion.get(i).getAsString());
		}
		return result;

	}
}
