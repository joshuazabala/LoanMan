package com.codefaucet.LoanMan.dto;

import java.util.ArrayList;
import java.util.List;

import com.codefaucet.LoanMan.common.EnumModuleAccessId;

public class UserProfileDTO {

    private long id;
    private boolean active;
    private String name;
    private String description;

    public List<EnumModuleAccessId> moduleAccessIds;

    public UserProfileDTO(long id, boolean active, String name, String description) {
	this.id = id;
	this.active = active;
	this.name = name;
	this.description = description;
	
	this.moduleAccessIds = new ArrayList<EnumModuleAccessId>();
    }

    public UserProfileDTO(String name, String description) {
	this(0L, true, name, description);
    }

    public UserProfileDTO() {
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

    public List<EnumModuleAccessId> getModuleAccessIds() {
	return moduleAccessIds;
    }

    public void setModuleAccessIds(List<EnumModuleAccessId> moduleAccessIds) {
	this.moduleAccessIds = moduleAccessIds;
    }

}
