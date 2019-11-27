package edu.utd.aos.gfs.servers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;

import org.tinylog.Logger;

import edu.utd.aos.gfs.references.GFSReferences;

public class MetaImpl {
	ArrayList<MetaQueue> queuedRequest;
	Integer createSentCounter;
	boolean createSentFlag;

	public MetaImpl() {
		super();
		this.queuedRequest = new ArrayList<MetaQueue>();
		this.createSentCounter = 0;
	}

	public ArrayList<MetaQueue> getQueuedRequest() {
		return queuedRequest;
	}

	public void setQueuedRequest(ArrayList<MetaQueue> queuedRequest) {
		this.queuedRequest = queuedRequest;
	}

	public Integer getCreateSentCounter() {
		return createSentCounter;
	}

	public boolean isCreateSentFlag() {
		return createSentFlag;
	}

	public void setCreateSentFlag(boolean createSentFlag) {
		this.createSentFlag = createSentFlag;
	}

	public void decCreateSentCounter() {
		this.createSentCounter--;
	}

	public void incCreateSentCounter() {
		this.createSentCounter++;
	}

	/**
	 * Choose a request from the deferred requests queue
	 * 
	 * @return deferred request
	 */
	public String chooseFromDeferredQueue() {
		Collections.sort(queuedRequest, MetaQueue.META_REQ_COMP);
		MetaQueue chosenRequest = queuedRequest.get(0);
		Logger.info("Next request served is:" + chosenRequest.getProcessNum() + "," + chosenRequest.getTimestamp());
		return chosenRequest.getMessage();
	}

	/**
	 * Remove from the deferred queue once the request is served
	 */
	public void deleteFromDeferredQueue() {
		this.queuedRequest.remove(0);
		Logger.info("Removed the served request, Queue size:" + this.queuedRequest.size());

	}

	public void addToDeferredQueue(String message) {
		String tokens[] = message.split(GFSReferences.REC_SEPARATOR);
		Timestamp timestamp = Timestamp.valueOf(tokens[2]);
		Integer clientNum = Integer.parseInt(tokens[3]);
		queuedRequest.add(new MetaQueue(clientNum, timestamp, message));
		Logger.info("Message was queued");

	}

}
