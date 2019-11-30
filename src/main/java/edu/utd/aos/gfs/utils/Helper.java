package edu.utd.aos.gfs.utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;

/**
 * @author pankaj
 * 
 *         Helper for all mundane tasks.
 */
public class Helper {

	/**
	 * Util method for sleep.
	 * 
	 * @param sec Seconds to sleep for.
	 * @throws GFSException Exception during sleep.
	 */
	public static void sleepForSec(int sec) throws GFSException {
		try {
			TimeUnit.SECONDS.sleep(sec);
		} catch (InterruptedException e) {
			throw new GFSException("Error while sleeping for: " + sec);
		}
	}

	/**
	 * Get command.
	 * 
	 * @param message Input message received on socket.
	 * @return the command.
	 */
	public static String getCommand(String received) {
		return received.split(GFSReferences.REC_SEPARATOR)[0];
	}

	/**
	 * Get message.
	 * 
	 * @param received Input message received on socket.
	 * @return the command.
	 */
	public static String getMessage(String received) {
		return received.split(GFSReferences.REC_SEPARATOR)[1];
	}

	/**
	 * Get third parameter of the message.
	 * 
	 * @param received Input message received on socket.
	 * @return the command.
	 */
	public static String getParamThree(String received) {
		return received.split(GFSReferences.REC_SEPARATOR)[2];
	}

	/**
	 * Get parsed Heartbeat messages.
	 * 
	 * @param received Input message received on socket.
	 * @return parsed Json HeartBeat Message.
	 */
	public static JsonObject getParsedHeartBeat(String message) {
		return new Gson().fromJson(message, JsonObject.class);
	}

	/**
	 * Sample iterator for JSON.
	 * 
	 * @param server
	 * @todo To be deleted.
	 * @param heartbeatJson
	 */
	public static void iterateHeartBeat(String server, JsonObject heartbeatJson) {

		for (Entry<String, JsonElement> entry : heartbeatJson.entrySet()) {
			String filename = entry.getKey();
			JsonObject chunkjObj = entry.getValue().getAsJsonObject();
			for (Entry<String, JsonElement> chunk : chunkjObj.entrySet()) {
				String chunkName = chunk.getKey();
				JsonArray sizeAndVersion = chunk.getValue().getAsJsonArray();
				String size = sizeAndVersion.get(0).getAsString();
				String version = sizeAndVersion.get(1).getAsString();
			}
			List<String> allChunks = new ArrayList<String>();
			JsonArray chunknames = entry.getValue().getAsJsonArray();
			for (JsonElement chunk : chunknames) {
				allChunks.add(chunk.getAsString());
			}
			Logger.debug("Filename: " + filename);
			Logger.debug("List of chunks: " + allChunks);
		}
	}

	/**
	 * Get Short Chunk Name from chunkServerName
	 * 
	 * @param chunkServername
	 * @return
	 */
	public static String getChunkName(String chunkServername) {
		return chunkServername.split("\\.")[0];
	}

	/**
	 * @return Current Timestamp of the node
	 */
	public static Timestamp getTimestamp() {
		return new Timestamp(new Date().getTime());
	}

	/**
	 * Compare timestamps from request
	 * 
	 * @param t1
	 * @param t2
	 * @param print
	 * @return
	 */
	public static int compareTimestamp(Timestamp t1, Timestamp t2, boolean print) {
		if (t1 == null && t2 == null)
			return 0;
		if (t1 != null && t2 == null)
			return -1;

		int c = 0;
		if (t1.after(t2)) {
			c = 1;
		} else if (t1.before(t2)) {
			c = -1;
		} else if (t1.equals(t2)) {
			c = 0;
		}
		return c;
	}

	public static boolean isQueueableMessage(String message) {
		String tokens[] = message.split(GFSReferences.REC_SEPARATOR);
		String command = tokens[0];
		if (GFSReferences.CREATE.equalsIgnoreCase(command) || GFSReferences.READ.equalsIgnoreCase(command)
				|| GFSReferences.APPEND.equalsIgnoreCase(command)) {
			Logger.info("Is a Queueable message");
			return true;
		} else {
			return false;
		}

	}

	public static int getTimeDifference(Timestamp start, Timestamp end) {
		long milliseconds = end.getTime() - start.getTime();
		int seconds = (int) milliseconds / 1000;
		return seconds;
	}

	/**
	 * Private constructor for utility classes.
	 */
	private Helper() {

	}
}
