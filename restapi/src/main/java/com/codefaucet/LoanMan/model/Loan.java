package com.codefaucet.LoanMan.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
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

    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(16) not null default 'ACTIVE'")
    private EnumLoanStatus status;

    @Column(columnDefinition = "decimal(18, 4) not null default 0")
    private double principal;
    
    @Column(columnDefinition = "decimal(18, 4) not null default 0")
    private double interest;
    
    @Column(columnDefinition = "decimal(18, 4) not null default 0")
    private double initialPayment;
    
    @Column(columnDefinition = "date not null")
    private LocalDate loanDate;
    
    @Column(columnDefinition = "date not null")
    private LocalDate paymentStartDate;

    @Column(columnDefinition = "integer not null default 12")
    private int terms;

    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "loan_type_id", nullable = false)
    private LoanType loanType;

    @ManyToOne
    @JoinColumn(name = "cutoff_profile_id", nullable = false)
    private CutoffProfile cutoffProfile;

    public Loan(Long id, boolean active, EnumLoanStatus status, double principal, double interest, double initialPayment,
	    int terms, LocalDate loanDate, LocalDate paymentStartDate) {
	this.id = id;
	this.active = active;
	this.status = status;
	this.principal = principal;
	this.interest = interest;
	this.initialPayment = initialPayment;
	this.terms = terms;
	this.loanDate = loanDate;
	this.paymentStartDate = paymentStartDate;

	payments = new ArrayList<>();
    }

    public Loan(double principal, double interest, double initialPayment, int terms, LocalDate loanDate,
	    LocalDate paymentStartDate) {
	this(0L, true, EnumLoanStatus.ACTIVE, principal, interest, initialPayment, terms, loanDate, paymentStartDate);
    }

    public Loan() {
	this(0, 0, 0, 12, LocalDate.now(), LocalDate.now());
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public boolean isActive() {
	return active;
    }

    public void setActive(boolean deleted) {
	this.active = deleted;
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

    public double getInterest() {
	return interest;
    }

    public void setInterest(double interest) {
	this.interest = interest;
    }

    public double getInitialPayment() {
	return initialPayment;
    }

    public void setInitialPayment(double initialPayment) {
	this.initialPayment = initialPayment;
    }

    public int getTerms() {
	return terms;
    }

    public void setTerms(int terms) {
	this.terms = terms;
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

    public CutoffProfile getCutoffProfile() {
	return cutoffProfile;
    }

    public void setCutoffProfile(CutoffProfile cutoffProfile) {
	this.cutoffProfile = cutoffProfile;
    }

    public double getPayable() {
	return getPrincipal() + (getPrincipal() * (getInterest() / 100));
    }

}
