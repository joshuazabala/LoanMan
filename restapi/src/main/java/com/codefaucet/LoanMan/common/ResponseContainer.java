package com.codefaucet.LoanMan.common;

import java.util.HashMap;
import java.util.Map;

public class ResponseContainer<T> {

    private T content;
    private EnumResponseStatus status;
    private Map<String, String> errorMap;
    private String message;

    public ResponseContainer() {
	status = EnumResponseStatus.SUCCESSFUL;
	errorMap = new HashMap<String, String>();
    }

    public T getContent() {
	return content;
    }

    public void setContent(T content) {
	this.content = content;
    }

    public EnumResponseStatus getStatus() {
	return status;
    }

    public void setStatus(EnumResponseStatus status) {
	this.status = status;
    }

    public Map<String, String> getErrorMap() {
	return errorMap;
    }

    public void setErrorMap(Map<String, String> errorMap) {
	this.errorMap = errorMap;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    public ResponseContainer<T> failed() {
	return this.failed("");
    }

    public ResponseContainer<T> failed(String message) {
	this.message = message;
	status = EnumResponseStatus.FAILED;
	return this;
    }
    
    public ResponseContainer<T> successful(T content) {
	this.content = content;
	status = EnumResponseStatus.SUCCESSFUL;
	return this;
    }
    
    public ResponseContainer<T> successful() {
	return this.successful(getContent());
    }
    
}
