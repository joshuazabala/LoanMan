package com.codefaucet.LoanMan.model;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.codefaucet.LoanMan.common.CutoffFrequency;

@Entity
@Table(name = "cutoff_profiles")
public class CutoffProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(16) not null default 'MONTHLY'")
    private CutoffFrequency frequency;

    @Column(length = 16, nullable = false, unique = true)
    private String code;

    @Column(length = 528)
    private String description;

    @Column(columnDefinition = "integer not null default 1")
    private int firstHalfStart;

    @Column(columnDefinition = "integer not null default 16")
    private int secondHalfStart;

    @OneToMany(mappedBy = "cutoffProfile", cascade = CascadeType.ALL)
    private List<Cutoff> cutoffs;

    public CutoffProfile(Long id, boolean active, CutoffFrequency frequency, String code, String description,
	    int firstHalfStart, int secondHalfStart) {
	this.id = id;
	this.active = active;
	this.frequency = frequency;
	this.code = code;
	this.description = description;
	this.firstHalfStart = firstHalfStart;
	this.secondHalfStart = secondHalfStart;

	cutoffs = new ArrayList<>();
    }

    public CutoffProfile(CutoffFrequency frequency, String code, String description, int firstHalfStart,
	    int secondHalfStart) {
	this(0L, true, frequency, code, description, firstHalfStart, secondHalfStart);
    }

    public CutoffProfile() {
	this(CutoffFrequency.MONTHLY, "", "", 1, 16);
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

    public CutoffFrequency getFrequency() {
	return frequency;
    }

    public void setFrequency(CutoffFrequency frequency) {
	this.frequency = frequency;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public int getFirstHalfStart() {
	return firstHalfStart;
    }

    public void setFirstHalfStart(int firstHalfStart) {
	this.firstHalfStart = firstHalfStart;
    }

    public int getSecondHalfStart() {
	return secondHalfStart;
    }

    public void setSecondHalfStart(int secondHalfStart) {
	this.secondHalfStart = secondHalfStart;
    }

    public List<Cutoff> getCutoffs() {
	return cutoffs;
    }

    public void setCutoffs(List<Cutoff> cutoffs) {
	this.cutoffs = cutoffs;
    }

}
