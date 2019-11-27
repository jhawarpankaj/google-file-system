package edu.utd.aos.gfs.servers.meta;

import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.aos.gfs.utils.Helper;

public class MetaHelperHeartbeat {
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
}
