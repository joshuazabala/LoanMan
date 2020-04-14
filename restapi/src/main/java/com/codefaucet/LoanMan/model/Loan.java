package com.codefaucet.LoanMan.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.codefaucet.LoanMan.common.EnumLoanStatus;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(16) not null default 'ACTIVE'")
    private EnumLoanStatus status;

    @Column(columnDefinition = "decimal(18, 4) not null default 0")
    private double principal;

    @Column(columnDefinition = "decimal(18, 4) not null default 0")
    private double payable;

    @Column(columnDefinition = "decimal(18, 4) not null default 0")
    private double amortization;

    @Column(columnDefinition = "date not null")
    private LocalDate loanDate;

    @Column(columnDefinition = "date not null")
    private LocalDate paymentStartDate;

    @Column(length = 2048)
    private String remarks;

    @OneToMany(mappedBy = "loan")
    private List<Payment> payments;

    @OneToMany(mappedBy = "loan")
    private List<Penalty> penalties;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "loan_type_id", nullable = false)
    private LoanType loanType;

    public Loan(Long id, EnumLoanStatus status, double principal, double payable, double amortization,
	    LocalDate loanDate, LocalDate paymentStartDate, String remarks) {
	this.id = id;
	this.status = status;
	this.principal = principal;
	this.payable = payable;
	this.amortization = amortization;
	this.loanDate = loanDate;
	this.paymentStartDate = paymentStartDate;
	this.remarks = remarks;

	payments = new ArrayList<>();
	penalties = new ArrayList<Penalty>();
    }

    public Loan(double principal, double payable, double amortization, LocalDate loanDate, LocalDate paymentStartDate,
	    String remarks) {
	this(0L, EnumLoanStatus.ACTIVE, principal, payable, amortization, loanDate, paymentStartDate, remarks);
    }

    public Loan() {
	this(0d, 0d, 0d, LocalDate.now(), LocalDate.now(), "");
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
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

    public List<Payment> getPayments() {
	return payments;
    }

    public void setPayments(List<Payment> payments) {
	this.payments = payments;
    }

    public Client getClient() {
	return client;
    }

    public void setClient(Client client) {
	this.client = client;
    }

    public LoanType getLoanType() {
	return loanType;
    }

    public void setLoanType(LoanType loanType) {
	this.loanType = loanType;
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

    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    public List<Penalty> getPenalties() {
	return penalties;
    }

    public void setPenalties(List<Penalty> penalties) {
	this.penalties = penalties;
    }

    @Override
    public String toString() {
	// TODO Auto-generated method stub
	return super.toString();
    }

}
