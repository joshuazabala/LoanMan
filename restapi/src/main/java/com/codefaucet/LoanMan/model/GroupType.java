package com.codefaucet.LoanMan.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "group_types")
public class GroupType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Column(length = 16, nullable = false, unique = true)
    private String code;

    @Column(length = 512, nullable = false)
    private String description;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<Group> groups;

    public GroupType(Long id, boolean active, String code, String name, String description) {
	this.id = id;
	this.active = active;
	this.code = code;
	this.description = description;
	groups = new ArrayList<>();
    }

    public GroupType(String code, String name, String description) {
	this(0L, true, code, name, description);
    }

    public GroupType() {
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

    public List<Group> getGroups() {
	return groups;
    }

    public void setGroups(List<Group> groups) {
	this.groups = groups;
    }

    @Override
    public String toString() {
	return code + " - " + description;
    }

}
