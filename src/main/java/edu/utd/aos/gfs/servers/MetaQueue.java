package edu.utd.aos.gfs.servers;

import java.sql.Timestamp;
import java.util.Comparator;

import edu.utd.aos.gfs.utils.Helper;

/**
 * Class object for Meta Deferred Request object
 */
public class MetaQueue implements Comparable<MetaQueue> {
	private int clientNum;
	private Timestamp timestamp;
	private String message;

	/**
	 * Constructor for creating a Quorum Deferred Request object
	 * 
	 * @param clientNum: clientNum of the deferred reply
	 * @param timestamp: timestamp of the request before it had been deferred
	 */
	public MetaQueue(int clientNum, Timestamp timestamp, String message) {
		super();
		this.clientNum = clientNum;
		this.timestamp = timestamp;
		this.message = message;
	}

	public int getProcessNum() {
		return clientNum;
	}

	public void setProcessNum(int processNum) {
		this.clientNum = processNum;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int compareTo(MetaQueue o) {
		return this.clientNum - o.getProcessNum();
	}

	/**
	 * Comparator class to sort the deferred reply queues. First sorted based on
	 * timestamp and then clientID, if timestamps are the same
	 */
	public static final Comparator<MetaQueue> META_REQ_COMP = new Comparator<MetaQueue>() {

		@Override
		public int compare(MetaQueue o1, MetaQueue o2) {
			int c = Helper.compareTimestamp(o1.getTimestamp(), o2.getTimestamp(), false);
			if (c == 0)
				c = o1.getProcessNum() - o2.getProcessNum();
			return c;
		}

	};
}
