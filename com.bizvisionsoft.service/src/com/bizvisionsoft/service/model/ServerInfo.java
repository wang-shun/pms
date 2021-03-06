package com.bizvisionsoft.service.model;

import java.util.Date;

public class ServerInfo {

	private String requester;

	private Date requestTime;

	private String hostMessage;

	private boolean debugEnabled;

	public ServerInfo(String requester) {
		this.requester = requester;
		this.requestTime = new Date();
	}

	public String getRequester() {
		return requester;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public String getHostMessage() {
		return hostMessage;
	}

	public ServerInfo setHostMessage(String hostMessage) {
		this.hostMessage = hostMessage;
		return this;
	}

	public ServerInfo seDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
		return this;
	}
	
	public boolean isDebugEnabled() {
		return debugEnabled;
	}
}
