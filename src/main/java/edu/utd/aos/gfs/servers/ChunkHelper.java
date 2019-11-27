package edu.utd.aos.gfs.servers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.LocalHost;

/**
 * Handles for all Chunk's mundane tasks.
 * @author pankaj
 *
 */
public class ChunkHelper {

	/**
	 * Parse create command and returns name of the file.
	 * @param received Input message
	 * @return Name of the file.
	 */
	public static String parseCreate(String received) {
		return received.split(GFSReferences.REC_SEPARATOR)[1];
	}
	
	/**
	 * Create a new chunk and a .version file with a number higher 
	 * than the last chunk.
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
			throw new GFSException("Error while creating new chunk: " + lastChunkNum
					+ " for file: " + fileName + ". Error: " + e);
		}		
	}
	
	/**
	 * Get the last chunk number for a file. 
	 * @param fileName Name of the file.
	 * @return 0 if no chunk or file present else the latest chunk number.
	 */
	private static int getLatestChunkNumber(String fileName) {
		String rootDir = LocalHost.getUniqueChunkPath() + fileName;
		File fileDir = new File(rootDir);
		if(!fileDir.exists()) return 0;
		ArrayList<File> allChunks = new ArrayList<File>(
			    Arrays.asList(fileDir.listFiles(File::isFile))
			);
		if(allChunks.isEmpty()) return 0;
		int max = 0;
		for(File chunk: allChunks) {
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
	 * @param filename Name of the file.
	 * @param content Content of the file.
	 * @return Formatted content message for the client.
	 */
	public static String prepareContentMessage(String filename, String content) {
		return GFSReferences.CONTENT + GFSReferences.SEND_SEPARATOR 
				+ filename + content;		
	}

	

}
