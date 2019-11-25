package edu.utd.aos.gfs.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.tinylog.Logger;

import edu.utd.aos.gfs.dto.ChunkServer;
import edu.utd.aos.gfs.references.GFSReferences;

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
		populateChunkServerCombos();
		for (List str : chunkServerCombos)
			System.out.println(str.get(0) + "," + str.get(1) + "," + str.get(2));
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
