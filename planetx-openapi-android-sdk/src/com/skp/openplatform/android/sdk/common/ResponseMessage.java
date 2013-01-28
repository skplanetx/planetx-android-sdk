package com.skp.openplatform.android.sdk.common;

public class ResponseMessage {
	private String statusCode;
	private String resultMessage;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}

	@Override
	public String toString() {
		return resultMessage;
	}
	
}
