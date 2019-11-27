package edu.utd.aos.gfs.servers.meta;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

import org.tinylog.Logger;

import edu.utd.aos.gfs.references.GFSReferences;
import edu.utd.aos.gfs.utils.Nodes;

public class MetaImpl {
	ArrayList<MetaQueue> queuedRequest;
	Integer createSentCounter;
	boolean createSentFlag;
	Integer padSentCounter;
	boolean padSentFlag;

	public MetaImpl() {
		super();
		this.queuedRequest = new ArrayList<MetaQueue>();
		this.createSentCounter = 0;
		this.padSentCounter = 0;
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
		String tokens[] = message.split(GFSReferences.REC_SEPARATOR);
		String command = tokens[0];
		Timestamp timestamp = null;
		if (GFSReferences.CREATE.equalsIgnoreCase(command)) {
			timestamp = Timestamp.valueOf(tokens[2]);
		} else if (GFSReferences.READ.equalsIgnoreCase(command)) {
			timestamp = Timestamp.valueOf(tokens[3]);
		} else if (GFSReferences.APPEND.equalsIgnoreCase(command)) {// TODO
			timestamp = Timestamp.valueOf(tokens[2]);
		}
		int clientId = Nodes.getClientIDByHostName(sender);
		ClientInfo ci = new ClientInfo(clientId, sender);
		queuedRequest.add(new MetaQueue(timestamp, message, ci));
		Logger.info("Message: " + message + " was queued, Size:" + getQueuedRequest().size());

	}

}
