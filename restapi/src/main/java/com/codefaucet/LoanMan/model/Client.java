package com.codefaucet.LoanMan.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Column(length = 16, nullable = false, unique = true)
    private String clientNumber;
    
    @Column(length = 32, nullable = false)
    private String lastName;
    
    @Column(length = 32, nullable = false)
    private String firstName;
    
    @Column(length = 32)
    private String middleName;
    
    @Column(length = 64)
    private String contactNumber;
    
    @Column(length = 128)
    private String emailAddress;
    
    @Column(columnDefinition = "varchar(512)")
    private String address;

    @ManyToMany()
    @JoinTable(name = "clients_groups", joinColumns = { @JoinColumn(name = "client_id") }, inverseJoinColumns = {
	    @JoinColumn(name = "group_id") })
    private List<Group> groups;
    
    public Client(Long id, boolean active, String clientNumber, String lastName, String firstName, String middleName,
	    String contactNumber, String emailAddress, String address) {
	this.id = id;
	this.active = active;
	this.clientNumber = clientNumber;
	this.lastName = lastName;
	this.firstName = firstName;
	this.middleName = middleName;
	this.contactNumber = contactNumber;
	this.emailAddress = emailAddress;
	this.address = address;
	
	groups = new ArrayList<Group>();
    }

    public Client(String clientNumber, String lastName, String firstName, String middleName, String contactNumber,
	    String emailAddress, String address) {
	this(0L, true, clientNumber, lastName, firstName, middleName, contactNumber, emailAddress, address);
    }

    public Client() {
	this("", "", "", "", "", "", "");
    }

    public String getName() {
	return getLastName() + ", " + getFirstName() + " " + getMiddleName();
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

    public String getClientNumber() {
	return clientNumber;
    }

    public void setClientNumber(String code) {
	this.clientNumber = code;
    }

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getMiddleName() {
	return middleName;
    }

    public void setMiddleName(String middleName) {
	this.middleName = middleName;
    }

    public String getContactNumber() {
	return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
	this.contactNumber = contactNumber;
    }

    public String getEmailAddress() {
	return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
	this.emailAddress = emailAddress;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

}
