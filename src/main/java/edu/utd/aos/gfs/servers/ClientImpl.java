package edu.utd.aos.gfs.servers;

public class ClientImpl {
	Integer appendSentCounter;
	boolean appendSentFlag;
	String appendMessage;

	public ClientImpl() {
		super();
		this.appendSentCounter = 0;
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

	public synchronized void decAppendSentCounter() {
		this.appendSentCounter--;
	}

	public void incAppendSentCounter() {
		this.appendSentCounter++;
	}
}
