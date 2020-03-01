package com.codefaucet.LoanMan.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class LoggingHelper {

    private final Logger logger = LogManager.getLogger();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public String asString(Object object) {
	try {
	    return objectMapper.writeValueAsString(object);
	} catch (Exception ex) {
	    logger.error("asString | Error: " + ex.getMessage(), ex);
	    return object == null ? "null" : object.toString();
	}
    }
    
}
