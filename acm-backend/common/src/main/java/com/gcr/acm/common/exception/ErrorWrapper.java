package com.gcr.acm.common.exception;

public class ErrorWrapper {
	private long timestamp = System.currentTimeMillis();
	private String errorMessage;
	private String exceptionName;

	public ErrorWrapper(String errorMessage, String exceptionName) {
		this.errorMessage = errorMessage;
		this.exceptionName = exceptionName;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}
}
