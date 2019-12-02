package edu.utd.aos.gfs.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.tinylog.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.servers.ClientHelper;
import edu.utd.aos.gfs.servers.ClientImpl;
import edu.utd.aos.gfs.utils.Helper;
import edu.utd.aos.gfs.utils.Nodes;

/*=============================================

=============================================
         AMTULS TESTER FILE
=============================================

=============================================*/
public class AmtulsTesterFile {
	public static final String READ = "READ";
	public static final String APPEND = "APPEND";
	public static final String CREATE = "CREATE";
	public static final String HEARTBEAT = "HEARTBEAT";
	public static List<List<ChunkServer>> chunkServerCombos = new ArrayList<List<ChunkServer>>();

	public static void main(String[] args) {
		// System.out.println(chooseAliveServer("a,b,c,d"));
		// String str = "\"1\"";
		// System.out.println(Integer.valueOf(str));
		ClientHelper.handleReadResponse("CONTENT||file||", new ClientImpl());
	}

	private static void mapToJson() {
		String jsonString = "";
		JsonObject main = new JsonObject();
		Gson gsonBuilder = new GsonBuilder().create();
		// Convert Java Map into JSON
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> one = new HashMap<String, String>();
		HashMap<String, String> two = new HashMap<String, String>();
		one.put("chunk1", "dc01");
		one.put("chunk2", "dc02");
		two.put("chunk3", "dc03");
		two.put("chunk4", "dc04");

		map.put("file1", one);
		map.put("file2", two);
		String jsonFromJavaMap = gsonBuilder.toJson(map);

		System.out.println(jsonFromJavaMap);
	}

	private static String chooseAliveServer(String servers) {
		String server = "";
		String serverlist[] = servers.split(",");
		for (String s : serverlist) {
			server = server + s + ",";
		}

		server = server.substring(0, server.length() - 1);
		return server;

	}

	private static String generateRandomWord(int datasize) {
		String randomString = "";
		Random random = new Random();
		char[] word = new char[datasize];
		for (int j = 0; j < word.length; j++) {
			word[j] = (char) ('a' + random.nextInt(26));
		}
		randomString = new String(word);
		return randomString;
	}

	public static void testArrayByte() {
		try {
			File file = new File("/Users/amtulnazneen/Downloads/hello.txt");
			byte[] bArray = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			fis.read(bArray);
			fis.close();
			for (int i = 4; i < bArray.length; i++) {
				System.out.print((char) bArray[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String generateRandomWord() {
		String randomString = "";
		Random random = new Random();

		char[] word = new char[random.nextInt(8) + 3];
		for (int j = 0; j < word.length; j++) {
			word[j] = (char) ('a' + random.nextInt(26));
		}
		randomString = new String(word);
		System.out.println(randomString);
		return randomString;
	}

	static void combinationUtil(int arr[], int data[], int start, int end, int index, int r) {
		if (index == r) {
			for (int j = 0; j < r; j++)
				System.out.print(data[j] + " ");
			// System.out.println("");
			return;
		}
		for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
			data[index] = arr[i];
			combinationUtil(arr, data, i + 1, end, index + 1, r);
		}
	}

	static void randomServer(List<ChunkServer> arr, List<ChunkServer> data, int start, int end, int index, int r) {
		if (index == r) {
			List<ChunkServer> li = new ArrayList<ChunkServer>();
			for (int j = 0; j < r; j++)
				li.add(data.get(j));
			chunkServerCombos.add(li);
			return;
		}
		for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
			data.add(index, arr.get(i));
			randomServer(arr, data, i + 1, end, index + 1, r);
		}
	}

	public static void populateChunkServerCombos() {
		List<ChunkServer> data = new ArrayList<ChunkServer>(3);
		List<ChunkServer> cs = new ArrayList<ChunkServer>();
		ChunkServer s1 = new ChunkServer();
		s1.setName("dc01");
		ChunkServer s2 = new ChunkServer();
		s2.setName("dc02");
		ChunkServer s3 = new ChunkServer();
		s3.setName("dc03");
		ChunkServer s4 = new ChunkServer();
		s4.setName("dc04");
		ChunkServer s5 = new ChunkServer();
		s5.setName("dc05");
		cs.add(s1);
		cs.add(s2);
		cs.add(s3);
		cs.add(s4);
		cs.add(s5);
		randomServer(cs, data, 0, 4, 0, 3);
	}

	public static void updateChunkCountFile(String fileName) {
		BufferedReader reader;
		String dir = Nodes.metaServerRootDir();
		String completeFilePath = dir + GFSReferences.CHUNK_FILE_COUNT + GFSReferences.FILE_EXT;
		try {
			reader = new BufferedReader(new FileReader(completeFilePath));
			String line = reader.readLine();
			String copyBack = "";
			while (line != null) {
				if (line.startsWith(fileName)) {
					String tokens[] = line.split(GFSReferences.MFILE_SEPARATOR);
					String count = tokens[1];
					Integer countInt = Integer.parseInt(count);
					countInt += 1;
					count = String.valueOf(countInt);
					String updatedLine = tokens[0] + GFSReferences.MFILE_SEPARATOR + count;
					copyBack += updatedLine + GFSReferences.NEW_LINE;
				} else
					copyBack += line + GFSReferences.NEW_LINE;
				line = reader.readLine();
			}
			reader.close();
			BufferedWriter writer = new BufferedWriter(new FileWriter(completeFilePath));
			writer.write(copyBack);
			writer.close();
		} catch (Exception e) {
			Logger.info(e);
		}
		Logger.info("ChunkCountFile Updated Entry-" + fileName);
	}

	public static void addEntryChunkCountFile(String fileName) {
		String dir = Nodes.metaServerRootDir();
		String completeFilePath = dir + GFSReferences.CHUNK_FILE_COUNT + GFSReferences.FILE_EXT;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(completeFilePath, true));
			String newEntry = fileName + GFSReferences.MFILE_SEPARATOR + 1;
			newEntry += GFSReferences.NEW_LINE;
			writer.write(newEntry);
			writer.close();
		} catch (Exception e) {
			Logger.info(e);
		}
		Logger.info("ChunkCountFile New Entry-" + fileName);
	}

	private static void updateMetaFileNewEntry(String completeFilePath, String fileName,
			List<ChunkServer> chunkServerNames) {
		String newEntry = "";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(completeFilePath));
			newEntry += fileName + GFSReferences.MFILE_SEPARATOR;
			for (ChunkServer chunk : chunkServerNames)
				newEntry += Helper.getChunkName(chunk.getName()) + GFSReferences.MCHUNK_SEPARATOR;
			newEntry = newEntry.substring(0, newEntry.length() - 1);
			writer.write(newEntry);
			writer.close();
		} catch (Exception e) {
			Logger.info(e);
		}
		Logger.info("MetaFile New Entry Added-" + newEntry);
	}
}
