package edu.utd.aos.gfs.servers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.LocalHost;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

/**
 * Handles for all Chunk's mundane tasks.
 * 
 * @author pankaj
 *
 */
public class ChunkHelper {

	public static byte[] buffer;

	/**
	 * Parse create command and returns name of the file.
	 * 
	 * @param received Input message
	 * @return Name of the file.
	 */
	public static String parseCreate(String received) {
		return received.split(GFSReferences.REC_SEPARATOR)[1];
	}

	/**
	 * Create a new chunk and a .version file with a number higher than the last
	 * chunk.
	 * 
	 * @param fileName Name of the file.
	 * @throws GFSException Error while creating a new chunk.
	 */
	public static void createNewChunk(String fileName) throws GFSException {

		String filePath = LocalHost.getUniqueChunkPath() + fileName + "/";
		String lastChunkNum = String.valueOf(getLatestChunkNumber(fileName) + 1);
		File chunkFile = new File(filePath + GFSReferences.CHUNK_PREFIX + lastChunkNum);
		File versionFile = new File(filePath + GFSReferences.CHUNK_PREFIX + lastChunkNum + ".version");
		try {
			FileUtils.touch(chunkFile);
			FileUtils.touch(versionFile);
			FileUtils.writeStringToFile(versionFile, "0", GFSReferences.ENCODING);
		} catch (IOException e) {
			throw new GFSException(
					"Error while creating new chunk: " + lastChunkNum + " for file: " + fileName + ". Error: " + e);
		}
	}

	/**
	 * Get the last chunk number for a file.
	 * 
	 * @param fileName Name of the file.
	 * @return 0 if no chunk or file present else the latest chunk number.
	 */
	private static int getLatestChunkNumber(String fileName) {
		String rootDir = LocalHost.getUniqueChunkPath() + fileName;
		File fileDir = new File(rootDir);
		if (!fileDir.exists())
			return 0;
		ArrayList<File> allChunks = new ArrayList<File>(Arrays.asList(fileDir.listFiles(File::isFile)));
		if (allChunks.isEmpty())
			return 0;
		int max = 0;
		for (File chunk : allChunks) {
			String chunkName = chunk.getName();
			Integer count = Integer.parseInt(chunkName.substring(chunkName.length() - 1));
			max = Math.max(count, max);
		}
		return max;
	}

	/**
	 * Private Constructor for utility classes.
	 */
	private ChunkHelper() {

	}

	/**
	 * Parse read command.
	 * 
	 * @param received
	 * @return
	 */
	public static Map<String, String> parseRead(String received) {
		String[] readDetails = received.split(GFSReferences.REC_SEPARATOR);
		Map<String, String> map = new HashMap<String, String>();
		map.put("filename", readDetails[1]);
		map.put("chunkname", readDetails[2]);
		map.put("offset", readDetails[3]);
		return map;
	}

	/**
	 * Parsed content message for READ response.
	 * 
	 * @param filename Name of the file.
	 * @param content  Content of the file.
	 * @return Formatted content message for the client.
	 */
	public static String prepareContentMessage(String filename, String content) {
		return GFSReferences.CONTENT + GFSReferences.SEND_SEPARATOR + filename + GFSReferences.SEND_SEPARATOR + content;
	}

	/**
	 * Parse the pad null.
	 * 
	 * @param received Input message.
	 * @return map.
	 */
	public static Map<String, String> parsePadNull(String received) {
		Map<String, String> map = new HashMap<String, String>();
		String[] padDetails = received.split(GFSReferences.REC_SEPARATOR);
		map.put("filename", padDetails[1]);
		map.put("chunkname", padDetails[2]);
		return map;
	}

	/**
	 * Pad null response.
	 * 
	 * @param fileNamePad
	 * @return String for PAD NULL ACK.
	 */
	public static String preparePadNullResponse(String fileNamePad) {
		return GFSReferences.PAD_NULL_ACK + GFSReferences.SEND_SEPARATOR + fileNamePad;
	}

	/**
	 * Parse create chunk message.
	 * 
	 * @param received Input message.
	 * @return Map of the parsed output.
	 */
	public static Map<String, String> parseCreateChunk(String received) {
		Map<String, String> map = new HashMap<String, String>();
		String[] createChunkDetails = received.split(GFSReferences.REC_SEPARATOR);
		map.put("filename", createChunkDetails[1]);
		map.put("chunkname", createChunkDetails[2]);
		return map;
	}

	public static String prepareCreateChunkResponse(String createChunkFileName) {
		return GFSReferences.CREATE_CHUNK_ACK + GFSReferences.SEND_SEPARATOR + createChunkFileName;
	}

	/**
	 * Update version of a file.
	 * 
	 * @param file
	 * @throws IOException
	 */
	public static void updateVersion(String filename) throws IOException {
		File file = new File(filename);
		byte[] bArray = new byte[(int) file.length()];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bArray);
		fis.close();
		String version = "";
		for (int i = 0; i < bArray.length; i++) {
			version = version + (char) bArray[i];
		}
		byte[] newVersion = String.valueOf(Integer.parseInt(version) + 1).getBytes();
		FileOutputStream output = new FileOutputStream(filename, false);
		output.write(newVersion);
		output.close();
	}

	/**
	 * Create a new version file.
	 * 
	 * @param string
	 * @throws IOException
	 */
	/*
	 * public static void createVersionFile(String version) throws IOException { //
	 * byte [] bytes = ByteBuffer.allocate(4).putInt(0).array(); FileOutputStream
	 * out = new FileOutputStream(version); out.write((byte) 0); out.close(); }
	 */
	public static void createVersionFile(String version) throws IOException {
//		byte [] bytes = ByteBuffer.allocate(4).putInt(0).array();		
		FileOutputStream out = new FileOutputStream(version);
		char c = '0';
//		Integer a = 0;
		byte b = (byte) c;
		out.write(b);
		out.close();
	}

	public static Map<String, String> parseAppend(String received) {
		Map<String, String> map = new HashMap<String, String>();
		String[] createChunkDetails = received.split(GFSReferences.REC_SEPARATOR);
		map.put("filename", createChunkDetails[1]);
		map.put("chunkname", createChunkDetails[2]);
		map.put("content", createChunkDetails[3]);
		return map;
	}

	public static byte[] getExistingBytes(File appendChunk) throws IOException {
		return FileUtils.readFileToByteArray(appendChunk);
	}

	public static byte[] getTotalBytes(byte[] b1, byte[] b2) {
		int len = b1.length + b2.length;
		byte[] result = new byte[len];
		int i = 0;
		for (; i < b1.length; i++) {
			result[i] = b1[i];
		}
		for (int j = 0; j < b2.length; j++, i++) {
			result[i] = b2[j];
		}
		return result;
	}

	/**
	 * Ready to append response.
	 * 
	 * @param fileNameAppend
	 * @return
	 */
	public static String prepareReadyToAppend(String fileNameAppend) {
		return GFSReferences.READY_TO_APPEND + GFSReferences.SEND_SEPARATOR + fileNameAppend;
	}

	/**
	 * Commit message.
	 * 
	 * @param received
	 * @return
	 */
	public static Map<String, String> parseCommit(String received) {
		Map<String, String> map = new HashMap<String, String>();
		String[] commitDetails = received.split(GFSReferences.REC_SEPARATOR);
		map.put("filename", commitDetails[1]);
		map.put("chunkname", commitDetails[2]);
		return map;
	}

	public static String prepareCommitAck(String filenameCommit) {
		return GFSReferences.COMMIT_ACK + GFSReferences.SEND_SEPARATOR + filenameCommit;
	}

	/**
	 * Parse incoming recover message.
	 * 
	 * @param received
	 */
	public static JsonObject parseRecover(String received) {
		String jsonResponse = received.split(GFSReferences.REC_SEPARATOR)[1];
		JsonObject recoverjObj = new Gson().fromJson(jsonResponse, JsonObject.class);
		return recoverjObj;
	}

	// RECOVER||{"file1": {"chunk1":"serverName", "chunk2":"serverName"}, "file2":
	// {"chunk3":"serverName"}}
	public static void updateReplicas(JsonObject parseRecover) {
		for (Entry<String, JsonElement> entry : parseRecover.entrySet()) {
			String filename = entry.getKey();
			JsonObject chunksToUpdate = entry.getValue().getAsJsonObject();
			for (Entry<String, JsonElement> chunks : chunksToUpdate.entrySet()) {
				String chunkname = chunks.getKey();
				String servername = chunks.getValue().getAsString();
				String prepareSendLatestData = ChunkHelper.prepareSendLatestData(filename, chunkname);
				Sockets.sendMessage(servername, Nodes.getPortByHostName(servername), prepareSendLatestData);
			}
		}
	}

	/**
	 * Prepare SEND_LATEST_DATA
	 * 
	 * @param filename
	 * @param chunkname
	 * @return
	 */
	private static String prepareSendLatestData(String filename, String chunkname) {
		return GFSReferences.SEND_LATEST_DATA + GFSReferences.SEND_SEPARATOR + filename + GFSReferences.SEND_SEPARATOR
				+ chunkname;
	}

	/**
	 * Parse incoming SEND_LATEST_DATA request.
	 * 
	 * @param received
	 */
	public static Map<String, String> parseSendLatestData(String received) {
		String[] split = received.split(GFSReferences.REC_SEPARATOR);
		Map<String, String> map = new HashMap<String, String>();
		map.put("filename", split[1]);
		map.put("chunkname", split[2]);
		return map;

	}

	/**
	 * Prepare get latest data string.
	 * 
	 * @return
	 */
	public static String prepareReceiveLatestData(String filename, String chunkname, String content, String version) {
		return GFSReferences.RECEIVE_LATEST_DATA + GFSReferences.SEND_SEPARATOR + filename
				+ GFSReferences.SEND_SEPARATOR + chunkname + GFSReferences.SEND_SEPARATOR + content
				+ GFSReferences.SEND_SEPARATOR + version;
	}

	/**
	 * Parse RECEIVE_LATEST_DATA command.
	 * 
	 * @param received
	 * @return
	 */
	public static Map<String, String> parseReceiveLatestData(String received) {
		Map<String, String> map = new HashMap<String, String>();
		String[] split = received.split(GFSReferences.REC_SEPARATOR);
		map.put("filename", split[1]);
		map.put("chunkname", split[2]);
		map.put("content", split[3]);
		map.put("version", split[4]);
		return map;

	}
}
