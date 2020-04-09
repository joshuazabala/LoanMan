package com.codefaucet.LoanMan.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentRequestDTO {

    private List<PaymentDTO> payments;
    private List<PenaltyDTO> penalties;

    private long loanId;
    private String loan;

    private String clientNumber;
    private String client;

    private LocalDate requestDate;
    
    public PaymentRequestDTO() {
	payments = new ArrayList<PaymentDTO>();
	penalties = new ArrayList<PenaltyDTO>();
	
	requestDate = LocalDate.now();
    }

    public List<PaymentDTO> getPayments() {
	return payments;
    }

    public void setPayments(List<PaymentDTO> payments) {
	this.payments = payments;
    }

    public List<PenaltyDTO> getPenalties() {
	return penalties;
    }

    public void setPenalties(List<PenaltyDTO> penalties) {
	this.penalties = penalties;
    }

    public long getLoanId() {
	return loanId;
    }

    public void setLoanId(long loanId) {
	this.loanId = loanId;
    }

    public String getLoan() {
	return loan;
    }

    public void setLoan(String loan) {
	this.loan = loan;
    }

    public String getClientNumber() {
	return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
	this.clientNumber = clientNumber;
    }

    public String getClient() {
	return client;
    }

    public void setClient(String client) {
	this.client = client;
    }

    public LocalDate getRequestDate() {
	return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
	this.requestDate = requestDate;
    }

}
