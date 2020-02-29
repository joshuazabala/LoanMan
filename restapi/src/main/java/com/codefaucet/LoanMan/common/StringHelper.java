package com.codefaucet.LoanMan.common;

public class StringHelper {

    public enum CharSet {
	ALPHABETIC,
	NUMERIC,
	ALPHANUMERIC
    }
    
    private static final String ALPHABETIC_CHAR_SET = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMERIC_CHAR_SET = "0123456789";
    private static final String ALPHANUMERIC_CHAR_SET = ALPHABETIC_CHAR_SET + NUMERIC_CHAR_SET;
    
    public static boolean isNullOrEmpty(String text) {
	return text == null || text.trim().isEmpty();
    }
    
    public static boolean isInCharSet(CharSet charSet, String text) {
	String targetCharSet = 
		charSet == CharSet.ALPHABETIC ? ALPHABETIC_CHAR_SET : 
		    charSet == CharSet.NUMERIC ? NUMERIC_CHAR_SET : 
			ALPHANUMERIC_CHAR_SET;
	
	// if numeric, consider negative and decimal
	if (charSet == CharSet.NUMERIC) {
	    int dashIndex = text.lastIndexOf("-");
	    if (dashIndex > 0) {
		return false; // negative sign should only be at start
	    }
	    
	    int dotRemovedLength = text.replaceAll(".", "").length();
	    if (dotRemovedLength == text.length() || dotRemovedLength == text.length() - 1) {
		return false; // dot (for decimal) should only appear once
	    }
	    text = text.replaceAll(".", "");
	    text = text.replaceAll("-", "");
	}
	
	return StringHelper.isInCharSet(targetCharSet.toLowerCase(), text.toLowerCase());
    }
    
    public static boolean isInCharSet(String charSet, String text) {
	for (char c : text.toCharArray()) {
	    int index = charSet.indexOf(c);
	    if (index == -1) {
		return false;
	    }
	}
	
	return true;
    }
    
}
