package com.codefaucet.LoanMan.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagedSearchResponse<T> {

    private List<T> content;
    private long totalPageCount;
    private EnumResponseStatus status;
    private String message;
    private Map<String, String> columnSorting;

    public PagedSearchResponse(List<T> content, long totalPageCount, EnumResponseStatus status, String message) {
	this.content = content;
	this.totalPageCount = totalPageCount;
	this.status = status;
	this.message = message;

	this.columnSorting = new HashMap<String, String>();
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

    public Map<String, String> getColumnSorting() {
	return columnSorting;
    }

    public void setColumnSorting(Map<String, String> columnSorting) {
	this.columnSorting = columnSorting;
    }

    public PagedSearchResponse<T> successful(List<T> content, long totalPageCount) {
	this.content = content;
	this.totalPageCount = totalPageCount;
	status = EnumResponseStatus.SUCCESSFUL;
	return this;
    }

    public PagedSearchResponse<T> successful() {
	return this.successful(getContent(), getTotalPageCount());
    }

    public PagedSearchResponse<T> failed(String message) {
	this.message = message;
	status = EnumResponseStatus.FAILED;
	return this;
    }

    public PagedSearchResponse<T> failed() {
	return this.failed("");
    }

}
