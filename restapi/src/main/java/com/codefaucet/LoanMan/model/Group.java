package com.codefaucet.LoanMan.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "`groups`")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Column(length = 16, nullable = false, unique = true)
    private String name;

    @Column(length = 512, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private GroupType type;

    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "groups")
    private List<Client> clients;

    public Group(Long id, boolean active, String name, String description) {
	this.id = id;
	this.active = active;
	this.name = name;
	this.description = description;
	clients = new ArrayList<>();
    }

    public Group(String code, String description) {
	this(0L, true, code, description);
    }

    public Group() {
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

    public void setName(String code) {
	this.name = code;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public GroupType getType() {
	return type;
    }

    public void setType(GroupType type) {
	this.type = type;
    }

    public List<Client> getClients() {
	return clients;
    }

    public void setClients(List<Client> clients) {
	this.clients = clients;
    }

}
