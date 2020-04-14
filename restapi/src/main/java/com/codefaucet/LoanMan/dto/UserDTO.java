package com.codefaucet.LoanMan.dto;

public class UserDTO {

    private long id;
    private boolean active;

    private String username;
    private String firstName;
    private String middleName;
    private String lastName;

    private String contactNumber;
    private String emailAddress;

    private long profileId;
    private String profile;

    public UserDTO(long id, boolean active, String username, String firstName, String middleName, String lastName,
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

    public UserDTO(String username, String firstName, String middleName, String lastName, String contactNumber,
	    String emailAddress) {
	this(0l, true, username, firstName, middleName, lastName, contactNumber, emailAddress);
    }

    public UserDTO() {
	this("", "", "", "", "", "");
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

    public String getUsername() {
	return username;
    }

    public void setUsername(String username) {
	this.username = username;
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

    public long getProfileId() {
	return profileId;
    }

    public void setProfileId(long profileId) {
	this.profileId = profileId;
    }

    public String getProfile() {
	return profile;
    }

    public void setProfile(String profile) {
	this.profile = profile;
    }

}
