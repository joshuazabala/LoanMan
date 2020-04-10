package com.codefaucet.LoanMan.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
    
    @Autowired
    private Environment environment;
    
    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;
    
    @GetMapping("/profileInfo")
    public String profileInfo() {
	String text = "";
	text += "time: " + LocalDateTime.now() + "<br />";
	text += "environment: " + String.join(", ", environment.getActiveProfiles()) + "<br />";
	text += "allowedOrigins: " + allowedOrigins;
	return text;
    }
    
}
