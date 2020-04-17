package com.codefaucet.LoanMan.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.codefaucet.LoanMan.common.EnumCutoffFrequency;
import com.codefaucet.LoanMan.common.EnumCutoffStatus;

@Entity
@Table(name = "cutoffs")
public class Cutoff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(16) not null default 'DRAFT'")
    private EnumCutoffStatus status;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(12) not null default 'MONTHLY'")
    private EnumCutoffFrequency frequency;

    @Column(columnDefinition = "date not null")
    private LocalDate startDate;

    @Column(columnDefinition = "date not null")
    private LocalDate endDate;

    @Column(columnDefinition = "integer not null")
    private int year;

    @Column(columnDefinition = "integer not null")
    private int month;

    @Column(name = "cutoff_number", columnDefinition = "integer not null default 1")
    private int cutoffNumber;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cutoff")
    private List<Payment> payments;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "cutoff")
    private List<Penalty> penalties;

    @Column(length = 512)
    private String remarks;

    public Cutoff(Long id, boolean active, EnumCutoffStatus status, EnumCutoffFrequency frequency, LocalDate startDate, LocalDate endDate, int year,
	    int month, int cutoffNumber, String remarks) {
	this.id = id;
	this.active = active;
	this.status = status;
	this.frequency = frequency;
	this.startDate = startDate;
	this.endDate = endDate;
	this.year = year;
	this.month = month;
	this.cutoffNumber = cutoffNumber;
	this.remarks = remarks;

	payments = new ArrayList<Payment>();
	penalties = new ArrayList<Penalty>();
    }

    public Cutoff() {
	this(EnumCutoffFrequency.MONTHLY, LocalDate.now(), LocalDate.now().plusMonths(1), LocalDate.now().getYear(),
		LocalDate.now().getMonthValue(), 1, "");
    }

    public Cutoff(EnumCutoffFrequency frequency, LocalDate startDate, LocalDate endDate, int year, int month,
	    int cutoffNumber, String remarks) {
	this(0l, true, EnumCutoffStatus.DRAFT, frequency, startDate, endDate, year, month, cutoffNumber, remarks);
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

    public EnumCutoffStatus getStatus() {
	return status;
    }

    public void setStatus(EnumCutoffStatus status) {
	this.status = status;
    }

    public LocalDate getStartDate() {
	return startDate;
    }

    public void setStartDate(LocalDate startDate) {
	this.startDate = startDate;
    }

    public LocalDate getEndDate() {
	return endDate;
    }

    public void setEndDate(LocalDate endDate) {
	this.endDate = endDate;
    }

    public int getYear() {
	return year;
    }

    public void setYear(int year) {
	this.year = year;
    }

    public int getMonth() {
	return month;
    }

    public void setMonth(int month) {
	this.month = month;
    }

    public int getCutoffNumber() {
	return cutoffNumber;
    }

    public void setCutoffNumber(int cutoffNumber) {
	this.cutoffNumber = cutoffNumber;
    }

    public EnumCutoffFrequency getFrequency() {
	return frequency;
    }

    public void setFrequency(EnumCutoffFrequency frequency) {
	this.frequency = frequency;
    }

    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    public List<Payment> getPayments() {
	return payments;
    }

    public void setPayments(List<Payment> payments) {
	this.payments = payments;
    }

    public List<Penalty> getPenalties() {
	return penalties;
    }

    public void setPenalties(List<Penalty> penalties) {
	this.penalties = penalties;
    }

}
