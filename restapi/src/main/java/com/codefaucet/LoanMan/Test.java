package com.codefaucet.LoanMan;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {

    private final Logger logger = LoggerFactory.getLogger(Test.class);

    public Test() throws IOException {
	boolean isReachable = isUrlReachable("http://10.0.2.230:8081/dax2-web/AmndWebService?WSDL");
	System.out.println("isUrlReachable: " + isReachable);
    }

    private boolean isUrlReachable(String url) throws IOException {
	try {
	    HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
	    int responseCode = urlConnection.getResponseCode();
	    urlConnection.disconnect();
	    return responseCode == 200;
	} catch (Exception ex) {
	    logger.error("isUrlReachable | Error: " + ex.getMessage(), ex);
	    return false;
	}
    }

    public static void main(String[] args) throws IOException {
	new Test();
    }

}
