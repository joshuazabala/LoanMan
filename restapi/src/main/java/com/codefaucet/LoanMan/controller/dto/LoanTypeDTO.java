package com.codefaucet.LoanMan.controller.dto;

public class LoanTypeDTO {

    private long id;
    private boolean active;
    private String code;
    private String description;

    public LoanTypeDTO(long id, boolean active, String code, String description) {
	this.id = id;
	this.active = active;
	this.code = code;
	this.description = description;
    }

    public LoanTypeDTO() {
	this(0l, true, "", "");
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public boolean isActive() {
	return active;
    }

    public void setActive(boolean active) {
	this.active = active;
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
