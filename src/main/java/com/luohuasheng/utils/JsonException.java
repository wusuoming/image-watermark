package com.luohuasheng.utils;

/**
 * Json 异常类
 * 
 * @version 2016年3月20日 | 0.0.1
 * @author panda
 */
public class JsonException extends RuntimeException {

	private static final long serialVersionUID = 649050807129119643L;

	public JsonException() {
		super();
	}

	public JsonException(String message) {
		super(message);
	}

	public JsonException(String message, Throwable cause) {
		super(message, cause);
	}

	public JsonException(Throwable cause) {
		super(cause);
	}

	protected JsonException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
