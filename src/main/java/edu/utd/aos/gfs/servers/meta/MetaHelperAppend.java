package edu.utd.aos.gfs.servers.meta;

import java.io.BufferedReader;
import java.io.FileReader;

import org.tinylog.Logger;

import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Nodes;

public class MetaHelperAppend {

	public static String getLastChunk(String fileName) {
		BufferedReader reader;
		String dir = Nodes.metaServerRootDir();
		String completeFilePath = dir + fileName;
		String lastline = "", result = "";
		try {
			reader = new BufferedReader(new FileReader(completeFilePath));
			String line = reader.readLine();
			while (line != null) {
				lastline = line;
				line = reader.readLine();
			}
			reader.close();
			String tokens[] = lastline.split(GFSReferences.MFILE_SEPARATOR);
			result = tokens[1];
		} catch (Exception e) {
			Logger.info(e);
		}
		return result;
	}
}
