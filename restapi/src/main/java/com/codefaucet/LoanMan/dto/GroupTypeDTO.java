package com.codefaucet.LoanMan.dto;

public class GroupTypeDTO {

    private long id;
    private boolean active;
    private String name;
    private String description;

    public GroupTypeDTO(long id, boolean active, String name, String description) {
	this.id = id;
	this.active = active;
	this.name = name;
	this.description = description;
    }

    public GroupTypeDTO() {
	this(0, true, "", "");
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

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

}
