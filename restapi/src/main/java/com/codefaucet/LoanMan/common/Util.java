package com.codefaucet.LoanMan.common;

public class Util {
    
    public static long getTotalPage(long totalCount, long pageSize) {
	long pageCount = 0;
	
	pageCount = totalCount / pageSize;
	if ((pageCount * pageSize) < totalCount) {
	    pageCount++;
	}
	
	return pageCount;
    }
    
}
