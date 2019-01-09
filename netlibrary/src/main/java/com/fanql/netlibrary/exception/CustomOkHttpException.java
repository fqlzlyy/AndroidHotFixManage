package com.fanql.netlibrary.exception;

public class CustomOkHttpException {
	private static final long serialVersionUID = 1L;
	public int code;
	public String message;
	public Exception mException;

	public CustomOkHttpException(int code, String message, Exception exception) {
		this.code = code;
		this.message = message;
		mException = exception;
	}
}