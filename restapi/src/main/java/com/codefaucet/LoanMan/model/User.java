package com.codefaucet.LoanMan.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "tinyint(1) not null default 1")
    private boolean active;

    @Column(length = 32, nullable = false, unique = true)
    private String username;
    @Column(length = 64, nullable = false)
    private String password;

    @Column(length = 32, nullable = false)
    private String firstName;
    @Column(length = 32)
    private String middleName;
    @Column(length = 32, nullable = false)
    private String lastName;

    @Column(length = 32)
    private String contactNumber;
    @Column(length = 32)
    private String emailAddress;

    @ManyToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private UserProfile profile;
    
    @OneToMany(mappedBy = "user")
    private List<Session> sessions;
    
    public User(Long id, boolean active, String username, String firstName, String middleName, String lastName,
	    String contactNumber, String emailAddress) {
	this.id = id;
	this.active = active;
	this.username = username;
	this.firstName = firstName;
	this.middleName = middleName;
	this.lastName = lastName;
	this.contactNumber = contactNumber;
	this.emailAddress = emailAddress;
    }

    public User(String username, String firstName, String middleName, String lastName, String contactNumber,
	    String emailAddress) {
	this(0L, true, username, firstName, middleName, lastName, contactNumber, emailAddress);
    }

    public User() {
	this("", "", "", "", "", "");
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

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
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

    public String getLastName() {
	return lastName;
    }

    public void setLastName(String lastName) {
	this.lastName = lastName;
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

    public UserProfile getProfile() {
	return profile;
    }

    public void setProfile(UserProfile profile) {
	this.profile = profile;
    }

}
