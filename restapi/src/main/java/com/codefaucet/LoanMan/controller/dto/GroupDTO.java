package com.codefaucet.LoanMan.controller.dto;

public class GroupDTO {

    private long id;
    private boolean active;
    private String code;
    private String description;

    private long groupTypeId;
    private String groupType;

    public GroupDTO(long id, boolean active, String code, String description, long groupTypeId, String groupType) {
	this.id = id;
	this.active = active;
	this.code = code;
	this.description = description;
	this.groupTypeId = groupTypeId;
	this.groupType = groupType;
    }

    public GroupDTO() {
	this(0l, true, "", "", 0l, "");
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

    public long getGroupTypeId() {
	return groupTypeId;
    }

    public void setGroupTypeId(long groupTypeId) {
	this.groupTypeId = groupTypeId;
    }

    public String getGroupType() {
	return groupType;
    }

    public void setGroupType(String groupType) {
	this.groupType = groupType;
    }

}
