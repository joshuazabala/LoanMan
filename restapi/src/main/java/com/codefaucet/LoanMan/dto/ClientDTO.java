package com.codefaucet.LoanMan.dto;

public class ClientDTO {

    private String id;
    private boolean active;
    private String firstName;
    private String middleName;
    private String lastName;
    private String contactNumber;
    private String emailAddress;
    private String address;

    public ClientDTO(String id, boolean active, String firstName, String middleName, String lastName,
	    String contactNumber, String emailAddress, String address) {
	this.id = id;
	this.active = active;
	this.firstName = firstName;
	this.middleName = middleName;
	this.lastName = lastName;
	this.contactNumber = contactNumber;
	this.emailAddress = emailAddress;
	this.address = address;
    }
    
    public ClientDTO(String firstName, String middleName, String lastName, String contactNumber, String emailAddress,
	    String address) {
	this("", true, firstName, middleName, lastName, contactNumber, emailAddress, address);
    }

    public ClientDTO() {
	this("", "", "", "", "", "");
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

    public void setActive(boolean active) {
	this.active = active;
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

    public String getAddress() {
	return address;
    }

    public void setAddress(String address) {
	this.address = address;
    }

}
