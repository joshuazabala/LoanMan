package com.codefaucet.LoanMan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codefaucet.LoanMan.repository.ISessionRepository;

@Service
public class SessionService {

    @Autowired
    private ISessionRepository sessionRepository;
    
}
