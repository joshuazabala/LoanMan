package com.codefaucet.LoanMan.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "loan_types")
public class LoanType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Column(length = 16, nullable = false, unique = true)
    private String code;

    @Column(length = 528, nullable = false)
    private String description;

    public LoanType(Long id, boolean deleted, String code, String name, String description) {
	this.id = id;
	this.active = deleted;
	this.code = code;
	this.description = description;
    }

    public LoanType(String code, String name, String description) {
	this(0L, true, code, name, description);
    }

    public LoanType() {
	this("", "", "");
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

}
