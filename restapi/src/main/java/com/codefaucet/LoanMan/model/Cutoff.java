package com.codefaucet.LoanMan.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.codefaucet.LoanMan.common.CutoffStatus;

@Entity
@Table(name = "cutoffs", uniqueConstraints = @UniqueConstraint(columnNames = { "cutoff_profile_id", "year", "month",
	"cutoff_number" }))
public class Cutoff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(16) not null default 'DRAFT'")
    private CutoffStatus status;

    @ManyToOne
    @JoinColumn(name = "cutoff_profile_id", nullable = false)
    private CutoffProfile cutoffProfile;

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

    public Cutoff() {
	this(CutoffStatus.DRAFT, null, LocalDate.now(), LocalDate.now().plusMonths(1), LocalDate.now().getYear(),
		LocalDate.now().getMonthValue(), 1);
    }

    public Cutoff(CutoffStatus status, CutoffProfile cutoffProfile, LocalDate startDate, LocalDate endDate, int year,
	    int month, int cutoffNumber) {
	this(0L, status, cutoffProfile, startDate, endDate, year, month, cutoffNumber);
    }

    public Cutoff(Long id, CutoffStatus status, CutoffProfile cutoffProfile, LocalDate startDate, LocalDate endDate,
	    int year, int month, int cutoffNumber) {
	this.id = id;
	this.status = status;
	this.cutoffProfile = cutoffProfile;
	this.startDate = startDate;
	this.endDate = endDate;
	this.year = year;
	this.month = month;
	this.cutoffNumber = cutoffNumber;
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public CutoffStatus getStatus() {
	return status;
    }

    public void setStatus(CutoffStatus status) {
	this.status = status;
    }

    public CutoffProfile getCutoffProfile() {
	return cutoffProfile;
    }

    public void setCutoffProfile(CutoffProfile cutoffProfile) {
	this.cutoffProfile = cutoffProfile;
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

}
