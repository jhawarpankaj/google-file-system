package edu.utd.aos.gfs.servers.meta;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.tinylog.Logger;

import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Nodes;

public class MetaImpl {
	ArrayList<MetaQueue> queuedRequest;

	Integer createSentCounter;
	boolean createSentFlag;

	Integer padSentCounter;
	boolean padSentFlag;

	boolean appendSentFlag;

	Integer createChunkSentCounter;
	boolean createChunkSentFlag;

	HashMap<String, Timestamp> chunkTimes;
	HashMap<String, String> chunkLiveness;

	public MetaImpl() {
		super();
		this.queuedRequest = new ArrayList<MetaQueue>();
		this.createSentCounter = 0;
		this.padSentCounter = 0;
		this.createChunkSentCounter = 0;
		this.chunkLiveness = new HashMap<String, String>();
		this.chunkTimes = new HashMap<String, Timestamp>();

	}

	public synchronized ArrayList<MetaQueue> getQueuedRequest() {
		return queuedRequest;
	}

	public void setQueuedRequest(ArrayList<MetaQueue> queuedRequest) {
		this.queuedRequest = queuedRequest;
	}

	public synchronized void setCreateSentCounter(Integer createSentCounter) {
		this.createSentCounter = createSentCounter;
	}

	public Integer getCreateSentCounter() {
		return createSentCounter;
	}

	public boolean isCreateSentFlag() {
		return createSentFlag;
	}

	public synchronized void setCreateSentFlag(boolean createSentFlag) {
		this.createSentFlag = createSentFlag;
	}

	public Integer getPadSentCounter() {
		return padSentCounter;
	}

	public synchronized void setPadSentCounter(Integer padSentCounter) {
		this.padSentCounter = padSentCounter;
	}

	public boolean isPadSentFlag() {
		return padSentFlag;
	}

	public synchronized void setPadSentFlag(boolean padSentFlag) {
		this.padSentFlag = padSentFlag;
	}

	public boolean isAppendSentFlag() {
		return appendSentFlag;
	}

	public void setAppendSentFlag(boolean appendSentFlag) {
		this.appendSentFlag = appendSentFlag;
	}

	public Integer getCreateChunkSentCounter() {
		return createChunkSentCounter;
	}

	public synchronized void setCreateChunkSentCounter(Integer createChunkSentCounter) {
		this.createChunkSentCounter = createChunkSentCounter;
	}

	public boolean isCreateChunkSentFlag() {
		return createChunkSentFlag;
	}

	public synchronized void setCreateChunkSentFlag(boolean createChunkSentFlag) {
		this.createChunkSentFlag = createChunkSentFlag;
	}

	public synchronized void decCreateChunkSentCounter() {
		this.createChunkSentCounter--;
	}

	public void incCreateChunkSentCounter() {
		this.createChunkSentCounter++;
	}

	public synchronized void decCreateSentCounter() {
		this.createSentCounter--;
	}

	public void incCreateSentCounter() {
		this.createSentCounter++;
	}

	public synchronized void decPadSentCounter() {
		this.padSentCounter--;
	}

	public void incPadSentCounter() {
		this.padSentCounter++;
	}

	/**
	 * Choose a request from the deferred requests queue
	 * 
	 * @return deferred request
	 */
	public MetaQueue chooseFromDeferredQueue() {
		Collections.sort(queuedRequest, MetaQueue.META_REQ_COMP);
		MetaQueue chosenRequest = queuedRequest.get(0);
		Logger.info("Next request served is:" + chosenRequest.getMessage() + " from "
				+ chosenRequest.getClient().getClientId() + "-@-" + chosenRequest.getTimestamp());
		return chosenRequest;
	}

	/**
	 * Remove from the deferred queue once the request is served
	 */
	public void deleteFromDeferredQueue() {
		this.queuedRequest.remove(0);
		Logger.info("Removed the served request, Queue size:" + this.queuedRequest.size());

	}

	public synchronized void addToDeferredQueue(String message, String sender) {
		Logger.info("Adding message to the Queue");
		String tokens[] = message.split(GFSReferences.REC_SEPARATOR);
		String command = tokens[0];
		Timestamp timestamp = null;
		if (GFSReferences.CREATE.equalsIgnoreCase(command)) {
			timestamp = Timestamp.valueOf(tokens[2]);
		} else if (GFSReferences.READ.equalsIgnoreCase(command)) {
			timestamp = Timestamp.valueOf(tokens[3]);
		} else if (GFSReferences.APPEND.equalsIgnoreCase(command)) {
			timestamp = Timestamp.valueOf(tokens[3]);
		}
		int clientId = Nodes.getClientIDByHostName(sender);
		ClientInfo ci = new ClientInfo(clientId, sender);
		queuedRequest.add(new MetaQueue(timestamp, message, ci));
		Logger.info("Message: " + message + " was queued, Size:" + getQueuedRequest().size());

	}

	public HashMap<String, Timestamp> getChunkTimes() {
		return chunkTimes;
	}

	public synchronized void setChunkTimes(HashMap<String, Timestamp> chunkTimes) {
		this.chunkTimes = chunkTimes;
	}

	public HashMap<String, String> getChunkLiveness() {
		return chunkLiveness;
	}

	public synchronized void setChunkLiveness(HashMap<String, String> chunkLiveness) {
		this.chunkLiveness = chunkLiveness;
	}

	public synchronized void updateChunkTimes(String server, Timestamp timestamp) {
		this.chunkTimes.put(server, timestamp);
	}

	public synchronized void updateChunkLiveness(String server, String state) {
		this.chunkLiveness.put(server, state);
	}

	public synchronized void removeDeadChunk(String server) {
		this.chunkLiveness.remove(server);
	}
}
