package com.codefaucet.LoanMan.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Column(columnDefinition = "date not null")
    private LocalDate date;

    @Column(columnDefinition = "decimal(18, 4) not null default 0")
    private double amount;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne
    @JoinColumn(name = "cutoff_id", nullable = false)
    private Cutoff cutoff;

    public Payment(Long id, boolean active, LocalDate date, double amount) {
	this.id = id;
	this.active = active;
	this.date = date;
	this.amount = amount;
    }

    public Payment(LocalDate date, double amount) {
	this(0L, true, date, amount);
    }

    public Payment() {
	this(LocalDate.now(), 0);
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

    public void setActive(boolean active) {
	this.active = active;
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

    public Loan getLoan() {
	return loan;
    }

    public void setLoan(Loan loan) {
	this.loan = loan;
    }

    public Cutoff getCutoff() {
	return cutoff;
    }

    public void setCutoff(Cutoff cutoff) {
	this.cutoff = cutoff;
    }

}
