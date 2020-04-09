package com.codefaucet.LoanMan.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codefaucet.LoanMan.common.EnumCutoffFrequency;
import com.codefaucet.LoanMan.model.Cutoff;
import com.codefaucet.LoanMan.service.CutoffService;

@RestController
@RequestMapping("/api")
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private CutoffService cutoffService;
    
    @GetMapping("/healthCheck")
    public String healthCheck() {
	logger.debug("healthCheck | Time: " + LocalDateTime.now());
	return "Server's up.";
    }
    
    @GetMapping("/testFindCutoff")
    public Cutoff testFindCutoff(@RequestParam String date, @RequestParam String cutoffFrequency) {
	DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	Cutoff cutoff = cutoffService.findCutoffByDate(LocalDate.parse(date, dateFormatter), EnumCutoffFrequency.valueOf(cutoffFrequency));
	return cutoff;
    }
    
}
