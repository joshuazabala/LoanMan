package com.codefaucet.LoanMan.dto;

import java.time.LocalDate;

public class PenaltyDTO {

    private long id;
    private LocalDate date;
    private double amount;

    private long loanId;
    private String loan;

    private long cutoffId;
    private String cutoff;

    public PenaltyDTO(long id, LocalDate date, double amount) {
	this.id = id;
	this.date = date;
	this.amount = amount;
    }

    public PenaltyDTO() {
	this(0l, LocalDate.now(), 0d);
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public LocalDate getDate() {
	return date;
    }

    public void setDate(LocalDate date) {
	this.date = date;
    }

    public double getAmount() {
	return amount;
    }

    public void setAmount(double amount) {
	this.amount = amount;
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

    public long getCutoffId() {
	return cutoffId;
    }

    public void setCutoffId(long cutoffId) {
	this.cutoffId = cutoffId;
    }

    public String getCutoff() {
	return cutoff;
    }

    public void setCutoff(String cutoff) {
	this.cutoff = cutoff;
    }

}
