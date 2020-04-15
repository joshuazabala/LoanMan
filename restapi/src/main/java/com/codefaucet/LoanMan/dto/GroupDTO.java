package com.codefaucet.LoanMan.dto;

public class GroupDTO {

    private long id;
    private boolean active;
    private String name;
    private String description;

    private long groupTypeId;
    private String groupType;

    public GroupDTO(long id, boolean active, String name, String description) {
	this.id = id;
	this.active = active;
	this.name = name;
	this.description = description;
    }

    public GroupDTO(String name, String description) {
	this(0l, true, name, description);
    }

    public GroupDTO() {
	this("", "");
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
