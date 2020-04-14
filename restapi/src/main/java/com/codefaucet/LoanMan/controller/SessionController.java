package com.codefaucet.LoanMan.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codefaucet.LoanMan.service.SessionService;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    @Autowired
    private SessionService sessionService;
    
}
