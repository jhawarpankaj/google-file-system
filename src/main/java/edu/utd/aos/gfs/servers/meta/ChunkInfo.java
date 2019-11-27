package edu.utd.aos.gfs.servers.meta;

import java.util.List;

public class ChunkInfo {
	List<ChunkServerInfo> chunkservers;
	String chunknum;
	int chunksize;

	public List<ChunkServerInfo> getChunkservers() {
		return chunkservers;
	}

	public void setChunkservers(List<ChunkServerInfo> chunkservers) {
		this.chunkservers = chunkservers;
	}

	public String getChunknum() {
		return chunknum;
	}

	public void setChunknum(String chunknum) {
		this.chunknum = chunknum;
	}

	public int getChunksize() {
		return chunksize;
	}

	public void setChunksize(int chunksize) {
		this.chunksize = chunksize;
	}

}
