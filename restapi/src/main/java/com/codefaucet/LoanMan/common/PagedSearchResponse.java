package com.codefaucet.LoanMan.common;

import java.util.ArrayList;
import java.util.List;

public class PagedSearchResponse<T> {

    private List<T> content;
    private long totalPageCount;
    private EnumResponseStatus status;
    private String message;

    public PagedSearchResponse(List<T> content, long totalPageCount, EnumResponseStatus status, String message) {
	this.content = content;
	this.totalPageCount = totalPageCount;
	this.status = status;
	this.message = message;
    }

    public PagedSearchResponse() {
	this(new ArrayList<T>(), 1, EnumResponseStatus.SUCCESSFUL, "");
    }

    public List<T> getContent() {
	return content;
    }

    public void setContent(List<T> content) {
	this.content = content;
    }

    public long getTotalPageCount() {
	return totalPageCount;
    }

    public void setTotalPageCount(long totalPageCount) {
	this.totalPageCount = totalPageCount;
    }

    public EnumResponseStatus getStatus() {
	return status;
    }

    public void setStatus(EnumResponseStatus status) {
	this.status = status;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

}
