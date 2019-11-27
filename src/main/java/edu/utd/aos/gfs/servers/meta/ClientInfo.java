package edu.utd.aos.gfs.servers.meta;

public class ClientInfo {
	private int clientId;
	private String hostname;

	public ClientInfo(int clientId, String hostname) {
		super();
		this.clientId = clientId;
		this.hostname = hostname;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
}
