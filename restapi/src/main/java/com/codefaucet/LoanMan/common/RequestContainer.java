package com.codefaucet.LoanMan.common;

import java.util.HashMap;
import java.util.Map;

public class RequestContainer<T> {

    private T content;
    private Map<String, Object> otherData;

    public RequestContainer() {
	otherData = new HashMap<String, Object>();
    }

    public T getContent() {
	return content;
    }

    public void setContent(T content) {
	this.content = content;
    }

    public Map<String, Object> getOtherData() {
	return otherData;
    }

    public void setOtherData(Map<String, Object> otherData) {
	this.otherData = otherData;
    }

}
