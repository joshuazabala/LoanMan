package com.codefaucet.LoanMan.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.codefaucet.LoanMan.common.EnumModuleAccessId;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Column(length = 32, nullable = false, unique = true)
    private String name;
    @Column(length = 512)
    private String description;

    @ElementCollection(targetClass = EnumModuleAccessId.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_profile_access_ids")
    @Column(name = "acess_id")
    private List<EnumModuleAccessId> moduleAccessIds;

    @OneToMany(mappedBy = "profile")
    private List<User> users;

    public UserProfile(Long id, boolean active, String name, String description) {
	this.id = id;
	this.active = active;
	this.name = name;
	this.description = description;
	this.moduleAccessIds = new ArrayList<EnumModuleAccessId>();
	this.users = new ArrayList<User>();
    }

    public UserProfile(String name, String description) {
	this(0L, true, name, description);
    }

    public UserProfile() {
	this("", "");
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

    public List<User> getUsers() {
	return users;
    }

    public void setUsers(List<User> users) {
	this.users = users;
    }

}
