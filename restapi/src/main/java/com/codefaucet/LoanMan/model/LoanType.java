package com.codefaucet.LoanMan.model;

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

@Entity
@Table(name = "loan_types")
public class LoanType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(length = 12, nullable = false)
    private EnumCutoffFrequency paymentFrequency;

    @Column(length = 16, nullable = false, unique = true)
    private String code;

    @Column(length = 528, nullable = false)
    private String description;

    @OneToMany(mappedBy = "loanType", fetch = FetchType.LAZY)
    private List<Loan> loans;

    public LoanType(Long id, boolean active, EnumCutoffFrequency paymentFrequency, String code, String name,
	    String description) {
	this.id = id;
	this.active = active;
	this.paymentFrequency = paymentFrequency;
	this.code = code;
	this.description = description;

	loans = new ArrayList<Loan>();
    }

    public LoanType(EnumCutoffFrequency paymentFrequency, String code, String name, String description) {
	this(0L, true, paymentFrequency, code, name, description);
    }

    public LoanType() {
	this(EnumCutoffFrequency.MONTHLY, "", "", "");
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

    public EnumCutoffFrequency getPaymentFrequency() {
	return paymentFrequency;
    }

    public void setPaymentFrequency(EnumCutoffFrequency paymentFrequency) {
	this.paymentFrequency = paymentFrequency;
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

    public List<Loan> getLoans() {
	return loans;
    }

    public void setLoans(List<Loan> loans) {
	this.loans = loans;
    }

}
