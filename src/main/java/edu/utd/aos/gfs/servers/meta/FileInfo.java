package edu.utd.aos.gfs.servers.meta;

import java.util.List;

public class FileInfo {
	String filename;
	List<ChunkInfo> chunks;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public List<ChunkInfo> getChunks() {
		return chunks;
	}

	public void setChunks(List<ChunkInfo> chunks) {
		this.chunks = chunks;
	}

}
