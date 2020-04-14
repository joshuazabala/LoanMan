package com.codefaucet.LoanMan.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

public class PagedSearchRequest {

    private String queryString;
    private boolean includeInactive;
    private int pageNumber;
    private int pageSize;
    private Map<String, Object> otherData;
    private Map<String, String> columnSorting;

    public PagedSearchRequest(String queryString, boolean includeInactive, int pageNumber, int pageSize) {
	this.queryString = queryString;
	this.includeInactive = includeInactive;
	this.pageNumber = pageNumber;
	this.pageSize = pageSize;

	otherData = new HashMap<String, Object>();
	columnSorting = new HashMap<String, String>();
    }

    public PagedSearchRequest() {
	this("", false, 1, 20);
    }

    public String getQueryString() {
	return queryString == null ? "" : queryString.trim();
    }

    public void setQueryString(String queryString) {
	this.queryString = queryString;
    }

    public boolean isIncludeInactive() {
	return includeInactive;
    }

    public void setIncludeInactive(boolean includeInactive) {
	this.includeInactive = includeInactive;
    }

    public int getPageNumber() {
	return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
	this.pageNumber = pageNumber;
    }

    public int getPageSize() {
	return pageSize;
    }

    public void setPageSize(int pageSize) {
	this.pageSize = pageSize;
    }

    public Map<String, Object> getOtherData() {
	return otherData;
    }

    public void setOtherData(Map<String, Object> otherData) {
	this.otherData = otherData;
    }

    public Map<String, String> getColumnSorting() {
	return columnSorting;
    }

    public void setColumnSorting(Map<String, String> columnSorting) {
	this.columnSorting = columnSorting;
    }

    public Pageable createPageable(Sort sort) {
	return PageRequest.of(pageNumber - 1, pageSize, sort);
    }

    public Pageable createPageable() {
	return this.createPageable(Sort.unsorted());
    }

    public List<Boolean> createStatusFilter() {
	List<Boolean> statusFilter = new ArrayList<Boolean>();
	statusFilter.add(true);
	if (includeInactive) {
	    statusFilter.add(false);
	}
	return statusFilter;
    }

    public Sort createSorter() {
	List<Order> orders = new ArrayList<Sort.Order>();
	columnSorting.forEach((key, value) -> {
	    if (value != null) {
		orders.add(value.equalsIgnoreCase("ascending") ? Order.asc(key) : Order.desc(key));
	    }
	});
	return Sort.by(orders);
    }
    
}
