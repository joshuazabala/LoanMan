package com.codefaucet.LoanMan.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.codefaucet.LoanMan.common.EnumSessionStatus;

@Entity
@Table(name = "sessions")
public class Session {

    @Id
    private String id;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(16) not null default 'ACTIVE'")
    private EnumSessionStatus status;

    @Column(length = 128, nullable = false)
    private String origin;
    @Column(columnDefinition = "timestamp not null")
    private LocalDateTime loginTime;
    @Column(columnDefinition = "timestamp not null")
    private LocalDateTime lastActivityTime;

    @Column(length = 512, nullable = false)
    private String remarks;

    @ManyToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public Session(String id, EnumSessionStatus status, String origin, LocalDateTime loginTime,
	    LocalDateTime lastActivityTime, String remarks) {
	super();
	this.id = id;
	this.status = status;
	this.origin = origin;
	this.loginTime = loginTime;
	this.lastActivityTime = lastActivityTime;
	this.remarks = remarks;
    }

    public Session(String origin, LocalDateTime loginTime) {
	this("", EnumSessionStatus.ACTIVE, origin, loginTime, loginTime, "");
    }

    public Session() {
	this("", LocalDateTime.now());
    }

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public EnumSessionStatus getStatus() {
	return status;
    }

    public void setStatus(EnumSessionStatus status) {
	this.status = status;
    }

    public String getOrigin() {
	return origin;
    }

    public void setOrigin(String origin) {
	this.origin = origin;
    }

    public LocalDateTime getLoginTime() {
	return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
	this.loginTime = loginTime;
    }

    public LocalDateTime getLastActivityTime() {
	return lastActivityTime;
    }

    public void setLastActivityTime(LocalDateTime lastActivityTime) {
	this.lastActivityTime = lastActivityTime;
    }

    public String getRemarks() {
	return remarks;
    }

    public void setRemarks(String remarks) {
	this.remarks = remarks;
    }

    public User getUser() {
	return user;
    }

    public void setUser(User user) {
	this.user = user;
    }

}
