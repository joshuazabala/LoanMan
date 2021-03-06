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
@Table(name = "penalties")
public class Penalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Penalty(Long id, LocalDate date, double amount) {
	this.id = id;
	this.date = date;
	this.amount = amount;
    }

    public Penalty(LocalDate date, double amount) {
	this(0L, date, amount);
    }

    public Penalty() {
	this(LocalDate.now(), 0D);
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Loan getLoan() {
	return loan;
    }

    public void setLoan(Loan loan) {
	this.loan = loan;
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

    public Cutoff getCutoff() {
	return cutoff;
    }

    public void setCutoff(Cutoff cutoff) {
	this.cutoff = cutoff;
    }

}
