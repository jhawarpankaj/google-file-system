package edu.utd.aos.gfs.servers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.io.FileUtils;
import org.tinylog.Logger;

import com.google.gson.JsonObject;

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
	public void run() {
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
					int offset = Integer.parseInt(parsedRead.get("offset"));
					String rootDir = LocalHost.getUniqueChunkPath() + filename + GFSReferences.PATH_SEPARATOR;
					File file = new File(rootDir + chunkname);
					byte[] bArray = new byte[(int) file.length()];
					FileInputStream fis = new FileInputStream(file);
					fis.read(bArray);
					fis.close();
					String content = "";
					for (int i = offset; i < bArray.length; i++) {
						content = content + (char) bArray[i];
					}
					String formattedContentMsg = ChunkHelper.prepareContentMessage(filename, content);
					Sockets.sendMessage(server, Nodes.getPortByHostName(server), formattedContentMsg);
					break;
				case GFSReferences.PAD_NULL:
					Map<String, String> parsedPadNull = ChunkHelper.parsePadNull(received);
					String fileNamePad = parsedPadNull.get("filename");
					String chunkNamePad = parsedPadNull.get("chunkname");
					String rootDirPad = LocalHost.getUniqueChunkPath() + fileNamePad + GFSReferences.PATH_SEPARATOR;
					File filePad = new File(rootDirPad + chunkNamePad);
	//					byte[] bArrayPad = new byte[(int) filePad.length()];
					byte[] bArrayPad = FileUtils.readFileToByteArray(filePad);
					String padNullResponse = ChunkHelper.preparePadNullResponse(fileNamePad);
					if (bArrayPad.length < GFSReferences.CHUNK_SIZE) {
						byte[] writeArray = new byte[GFSReferences.CHUNK_SIZE];
						int i = 0;
						for (; i < bArrayPad.length; i++) {
							writeArray[i] = bArrayPad[i];
						}
						char c = '\0';
						for (; i < GFSReferences.CHUNK_SIZE - bArrayPad.length; i++) {
							writeArray[i] = (byte) c;
						}
						ChunkHelper.updateVersion(rootDirPad + chunkNamePad + ".version");
						FileUtils.writeByteArrayToFile(filePad, writeArray);
						Logger.debug("Length of the file after appending null: " + filePad.length());
					}
					Chunk.sendAHeartBeat();
					Sockets.sendMessage(server, Nodes.getPortByHostName(server), padNullResponse);
					break;
				case GFSReferences.CREATE_CHUNK:
					Map<String, String> parsedCreateChunk = ChunkHelper.parseCreateChunk(received);
					String createChunkFileName = parsedCreateChunk.get("filename");
					String createChunkName = parsedCreateChunk.get("chunkname");
					String createChunkRootDir = LocalHost.getUniqueChunkPath() + createChunkFileName
							+ GFSReferences.PATH_SEPARATOR;
					File fileDir = new File(createChunkRootDir);
					if (!fileDir.exists()) {
						fileDir.mkdirs();
					}
					File createChunkFile = new File(createChunkRootDir + createChunkName);
					ChunkHelper.createVersionFile(createChunkRootDir + createChunkName + ".version");
					FileUtils.touch(createChunkFile);
					String createChunkResponse = ChunkHelper.prepareCreateChunkResponse(createChunkFileName);
					Chunk.sendAHeartBeat();
					Sockets.sendMessage(server, Nodes.getPortByHostName(server), createChunkResponse);
					break;
				case GFSReferences.APPEND:
					Map<String, String> parsedAppendChunk = ChunkHelper.parseAppend(received);
					String fileNameAppend = parsedAppendChunk.get("filename");
					String chunkNameAppend = parsedAppendChunk.get("chunkname");
					String appendContent = parsedAppendChunk.get("content");
					String appendChunkRootDir = LocalHost.getUniqueChunkPath() + fileNameAppend
							+ GFSReferences.PATH_SEPARATOR;
					File appendChunk = new File(appendChunkRootDir + chunkNameAppend);
					byte[] currentBytesAppend = ChunkHelper.getExistingBytes(appendChunk);
					byte[] newContent = appendContent.getBytes();
					ChunkHelper.buffer = ChunkHelper.getTotalBytes(currentBytesAppend, newContent);
					String prepareReadyToAppend = ChunkHelper.prepareReadyToAppend(fileNameAppend);
					Sockets.sendMessage(server, Nodes.getPortByHostName(server), prepareReadyToAppend);
					break;
				case GFSReferences.COMMIT:
					Map<String, String> parsedCommit = ChunkHelper.parseCommit(received);
					String filenameCommit = parsedCommit.get("filename");
					String chunkNameCommit = parsedCommit.get("chunkname");
					String commitRootDir = LocalHost.getUniqueChunkPath() + filenameCommit + GFSReferences.PATH_SEPARATOR;
					ChunkHelper.updateVersion(commitRootDir + chunkNameCommit + ".version");
					FileUtils.writeByteArrayToFile(new File(commitRootDir + chunkNameCommit), ChunkHelper.buffer);
					Chunk.sendAHeartBeat();
					String prepareCommitAck = ChunkHelper.prepareCommitAck(filenameCommit);
					Sockets.sendMessage(server, Nodes.getPortByHostName(server), prepareCommitAck);
					break;
						
				case GFSReferences.RECOVER:
					JsonObject parseRecover = ChunkHelper.parseRecover(received);
					ChunkHelper.updateReplicas(parseRecover);
					break;
					
				case GFSReferences.SEND_LATEST_DATA:
					Map<String, String> parseSendLatestData = ChunkHelper.parseSendLatestData(received);
					String fileNameData = parseSendLatestData.get("filename");
					String chunkNameData = parseSendLatestData.get("chunkname");
					String chunkDataToSend = LocalHost.getUniqueChunkPath() + fileNameData + 
							GFSReferences.PATH_SEPARATOR + chunkNameData;
					String chunkVersionToSend = LocalHost.getUniqueChunkPath() + fileNameData + 
							GFSReferences.PATH_SEPARATOR + chunkNameData + ".version";
					byte[] fileBytes = FileUtils.readFileToByteArray(new File(chunkDataToSend));
					byte[] versionBytes = FileUtils.readFileToByteArray(new File(chunkVersionToSend));
					String latestContent = new String(fileBytes);
					String latestVersion = new String(versionBytes);
					String getLatestData = ChunkHelper.prepareReceiveLatestData(fileNameData, chunkNameData,
							latestContent, latestVersion);
					Sockets.sendMessage(server, Nodes.getPortByHostName(server), getLatestData);
					break;
					
				case GFSReferences.RECEIVE_LATEST_DATA:
					Map<String, String> parseReceiveLatestData = ChunkHelper.parseReceiveLatestData(received);
					String recFileName = parseReceiveLatestData.get("filename");
					String recChunkName = parseReceiveLatestData.get("chunkname");
					String recContent = parseReceiveLatestData.get("content");
					String recVersion = parseReceiveLatestData.get("version");
					String chunkFileToUpdate = LocalHost.getUniqueChunkPath() + recFileName
							+ GFSReferences.PATH_SEPARATOR + recChunkName;
					String versionFileToUpdate = chunkFileToUpdate + ".version";
					byte[] bytesToWriteChunk = recContent.getBytes();
					byte[] bytesToWriteVersion = recVersion.getBytes();
					FileUtils.writeByteArrayToFile(new File(chunkFileToUpdate), bytesToWriteChunk);
					FileUtils.writeByteArrayToFile(new File(versionFileToUpdate), bytesToWriteVersion);
					break;
						
				default:
					throw new GFSException("Unidentified input: " + command 
							+ " received on CHUNK server!!");
			}
		} catch (Exception e) {
			Logger.error("Error while performing client request: " + e);
		}
	}
}