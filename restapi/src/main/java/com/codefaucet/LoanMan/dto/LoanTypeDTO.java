package com.codefaucet.LoanMan.dto;

import com.codefaucet.LoanMan.common.EnumCutoffFrequency;

public class LoanTypeDTO {

    private long id;
    private boolean active;
    private EnumCutoffFrequency paymentFrequency;
    private String name;
    private String description;

    public LoanTypeDTO(long id, boolean active, EnumCutoffFrequency paymentFrequency, String name, String description) {
	this.id = id;
	this.active = active;
	this.paymentFrequency = paymentFrequency;
	this.name = name;
	this.description = description;
    }

    public LoanTypeDTO(EnumCutoffFrequency paymentFrequency, String name, String description) {
	this(0l, true, paymentFrequency, name, description);
    }

    public LoanTypeDTO() {
	this(EnumCutoffFrequency.MONTHLY, "", "");
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

    public EnumCutoffFrequency getPaymentFrequency() {
	return paymentFrequency;
    }

    public void setPaymentFrequency(EnumCutoffFrequency paymentFrequency) {
	this.paymentFrequency = paymentFrequency;
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

}
