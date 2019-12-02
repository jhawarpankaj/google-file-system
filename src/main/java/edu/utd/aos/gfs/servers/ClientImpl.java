package edu.utd.aos.gfs.servers;

public class ClientImpl {
	Integer appendSentCounter;
	boolean appendSentFlag;
	String appendMessage;
	Integer commitSentCounter;
	boolean commitSendFlag;
	boolean receivedReadResponse;

	public ClientImpl() {
		super();
		this.appendSentCounter = 0;
		this.commitSentCounter = 0;
		this.receivedReadResponse = false;
	}

	public Integer getAppendSentCounter() {
		return appendSentCounter;
	}

	public synchronized void setAppendSentCounter(Integer appendSentCounter) {
		this.appendSentCounter = appendSentCounter;
	}

	public boolean isAppendSentFlag() {
		return appendSentFlag;
	}

	public synchronized void setAppendSentFlag(boolean appendSentFlag) {
		this.appendSentFlag = appendSentFlag;
	}

	public String getAppendMessage() {
		return appendMessage;
	}

	public void setAppendMessage(String appendMessage) {
		this.appendMessage = appendMessage;
	}

	public Integer getCommitSentCounter() {
		return commitSentCounter;
	}

	public synchronized void setCommitSentCounter(Integer commitSentCounter) {
		this.commitSentCounter = commitSentCounter;
	}

	public boolean isCommitSendFlag() {
		return commitSendFlag;
	}

	public synchronized void setCommitSendFlag(boolean commitSendFlag) {
		this.commitSendFlag = commitSendFlag;
	}

	public boolean isReceivedReadResponse() {
		return receivedReadResponse;
	}

	public synchronized void setReceivedReadResponse(boolean receivedReadResponse) {
		this.receivedReadResponse = receivedReadResponse;
	}

	public synchronized void decAppendSentCounter() {
		this.appendSentCounter--;
	}

	public void incAppendSentCounter() {
		this.appendSentCounter++;
	}

	public void incCommitSentCounter() {
		this.commitSentCounter++;
	}

	public synchronized void decCommitSentCounter() {
		this.commitSentCounter--;
	}

}
