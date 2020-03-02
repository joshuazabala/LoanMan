package com.codefaucet.LoanMan.controller.dto;

import java.time.LocalDate;

import com.codefaucet.LoanMan.common.EnumCutoffFrequency;
import com.codefaucet.LoanMan.common.EnumCutoffStatus;

public class CutoffDTO {

    private long id;
    private EnumCutoffStatus status;
    private EnumCutoffFrequency frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private int year;
    private int month;
    private int cutoffNumber;

    public CutoffDTO(long id, EnumCutoffStatus status, EnumCutoffFrequency frequency, LocalDate startDate,
	    LocalDate endDate, int year, int month, int cutoffNumber) {
	this.id = id;
	this.status = status;
	this.frequency = frequency;
	this.startDate = startDate;
	this.endDate = endDate;
	this.year = year;
	this.month = month;
	this.cutoffNumber = cutoffNumber;
    }

    public CutoffDTO() {
	this(0l, EnumCutoffStatus.DRAFT, EnumCutoffFrequency.MONTHLY, LocalDate.now(), LocalDate.now(), 2020, 3, 1);
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public EnumCutoffStatus getStatus() {
	return status;
    }

    public void setStatus(EnumCutoffStatus status) {
	this.status = status;
    }

    public EnumCutoffFrequency getFrequency() {
	return frequency;
    }

    public void setFrequency(EnumCutoffFrequency frequency) {
	this.frequency = frequency;
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
