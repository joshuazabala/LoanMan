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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.codefaucet.LoanMan.common.StringHelper;

@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    @GenericGenerator(name = "client_seq", strategy = "com.codefaucet.LoanMan.common.ClientNumberGenerator")
    @Column(length = 12)
    private String id;

    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

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

    @OneToMany(mappedBy = "client")
    private List<Loan> loans;

    public Client(String id, boolean active, String lastName, String firstName, String middleName, String contactNumber,
	    String emailAddress, String address) {
	this.id = id;
	this.active = active;
	this.lastName = lastName;
	this.firstName = firstName;
	this.middleName = middleName;
	this.contactNumber = contactNumber;
	this.emailAddress = emailAddress;
	this.address = address;

	groups = new ArrayList<Group>();
	loans = new ArrayList<Loan>();
    }

    public Client(String clientNumber, String lastName, String firstName, String middleName, String contactNumber,
	    String emailAddress, String address) {
	this("", true, lastName, firstName, middleName, contactNumber, emailAddress, address);
    }

    public Client() {
	this("", "", "", "", "", "", "");
    }

    public String getName() {
	return getLastName() + ", " + getFirstName() + (StringHelper.isNullOrEmpty(middleName) ? "" : " " + middleName);
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public boolean isActive() {
	return active;
    }

    public void setActive(boolean deleted) {
	this.active = deleted;
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

    public List<Loan> getLoans() {
	return loans;
    }

    public void setLoans(List<Loan> loans) {
	this.loans = loans;
    }

}
