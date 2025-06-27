package com.manager.model;

import org.springframework.http.HttpStatus;

public class ResponseMessage<T> {
	private Integer code;
	private String message;
	private T data;
	public ResponseMessage() {
	}
	public ResponseMessage(Integer code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
	public static <T> ResponseMessage<T> success(String message,T data) {
		return new ResponseMessage<>(200, message, data);
	}
	public static <T> ResponseMessage<T> error(Integer code, String message) {
		return new ResponseMessage<>(HttpStatus.BAD_REQUEST.value(), message, null);
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	@Override
	public String toString() {
		return "ResponseMessage [code=" + code + ", message=" + message + ", data=" + data + "]";
	}

	
}
