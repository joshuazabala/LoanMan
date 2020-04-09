package com.codefaucet.LoanMan.dto;

import java.time.LocalDate;

import com.codefaucet.LoanMan.common.EnumLoanStatus;

public class LoanDTO {

    private long id;
    private EnumLoanStatus status;
    private double principal;
    private double payable;
    private double amortization;
    private LocalDate loanDate;
    private LocalDate paymentStartDate;
    private String remarks;

    private String clientId;
    private String client;

    private long loanTypeId;
    private String loanType;

    public LoanDTO(long id, EnumLoanStatus status, double principal, double payable, double amortization,
	    LocalDate loanDate, LocalDate paymentStartDate, String remarks) {
	this.id = id;
	this.status = status;
	this.principal = principal;
	this.payable = payable;
	this.amortization = amortization;
	this.loanDate = loanDate;
	this.paymentStartDate = paymentStartDate;
	this.remarks = remarks;
    }

    public LoanDTO() {
	this(0l, EnumLoanStatus.ACTIVE, 0d, 0d, 0d, LocalDate.now(), LocalDate.now(), "");
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public EnumLoanStatus getStatus() {
	return status;
    }

    public void setStatus(EnumLoanStatus status) {
	this.status = status;
    }

    public double getPrincipal() {
	return principal;
    }

    public void setPrincipal(double principal) {
	this.principal = principal;
    }

    public double getPayable() {
	return payable;
    }

    public void setPayable(double payable) {
	this.payable = payable;
    }

    public double getAmortization() {
	return amortization;
    }

    public void setAmortization(double amortization) {
	this.amortization = amortization;
    }

    public LocalDate getLoanDate() {
	return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
	this.loanDate = loanDate;
    }

    public LocalDate getPaymentStartDate() {
	return paymentStartDate;
    }

    public void setPaymentStartDate(LocalDate paymentStartDate) {
	this.paymentStartDate = paymentStartDate;
    }

    public String getClientId() {
	return clientId;
    }

    public void setClientId(String clientId) {
	this.clientId = clientId;
    }

    public String getClient() {
	return client;
    }

    public void setClient(String client) {
	this.client = client;
    }

    public long getLoanTypeId() {
	return loanTypeId;
    }

    public void setLoanTypeId(long loanTypeId) {
	this.loanTypeId = loanTypeId;
    }

    public String getLoanType() {
	return loanType;
    }

    public void setLoanType(String loanType) {
	this.loanType = loanType;
    }

    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

}
