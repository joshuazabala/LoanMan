package com.codefaucet.LoanMan.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/healthCheck")
    public String healthCheck() {
	return "Server's up.";
    }
    
}
