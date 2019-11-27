package edu.utd.aos.gfs.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.tinylog.Logger;

import edu.utd.aos.gfs.exception.GFSException;
import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Helper;
import edu.utd.aos.gfs.utils.LocalHost;
import edu.utd.aos.gfs.utils.Nodes;
import edu.utd.aos.gfs.utils.Sockets;

public class ChunkListener extends Thread {
	final Socket worker;
	final DataInputStream dis;
    final DataOutputStream dos;
    public static final ReentrantLock lock = new ReentrantLock();
    
    public ChunkListener(Socket worker, DataInputStream dis, DataOutputStream dos) {
    	this.worker = worker;
    	this.dis = dis;
    	this.dos = dos;
    }
    
    @Override
    public void run(){
    	try {
    		// message received on socket.
			String received = dis.readUTF();
			// server from which the message has come.
			String server = this.worker.getInetAddress().getHostName();
			Logger.debug("Received message: " + received);
			String command = Helper.getCommand(received);
			
			switch (command) {	
			
				case GFSReferences.CREATE:
					String fileName = ChunkHelper.parseCreate(received);
					ChunkHelper.createNewChunk(fileName);
					Chunk.sendAHeartBeat();
					Sockets.sendMessage(Nodes.metaServerName(), Nodes.metaServerPort(), GFSReferences.CREATE_ACK);
					break;
				
				case GFSReferences.READ:
					Map<String, String> parsedRead = ChunkHelper.parseRead(received);
					String filename = parsedRead.get("filename");
					String chunkname = parsedRead.get("chunkname");
					long offset = Long.parseLong(parsedRead.get("offset"));
					String rootDir = LocalHost.getUniqueChunkPath() + 
							 filename + GFSReferences.PATHSEPARATOR;					
					File file = new File(rootDir + chunkname);										
					RandomAccessFile raf = new RandomAccessFile(file, "r");
					raf.seek(offset);
					String content = raf.readUTF();
					String formattedContentMsg = ChunkHelper.prepareContentMessage(filename, content);
					Sockets.sendMessage(server, Nodes.getPortByHostName(server), formattedContentMsg);					
					break;
					
				default:
					throw new GFSException("Unidentified input: " + command 
							+ " received on CHUNK server!!");
			}
		}catch(Exception e) {
			Logger.error("Error while performing client request: " + e);
		}
   	}
}

