package com.bsms.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "MB_smsactivation")
@Data
public class MbSmsActivation {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private String id;
	@Column(name="msisdn")
	private String msisdn;
	@Column(name="message")
	private String message;
	@Column(name="date_received")
	private String dateReceived;
	@Column(name="isverified")
	private String isverified;
	@Column(name="date_verified")
	private String dateVerified;
	
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
//	public String getMsisdn() {
//		return msisdn;
//	}
//	public void setMsisdn(String msisdn) {
//		this.msisdn = msisdn;
//	}
//	public String getMessage() {
//		return message;
//	}
//	public void setMessage(String message) {
//		this.message = message;
//	}
//	public String getIsverified() {
//		return isverified;
//	}
//	public void setIsverified(String isverified) {
//		this.isverified = isverified;
//	}
//	public String getDate_received() {
//		return date_received;
//	}
//	public void setDate_received(String date_received) {
//		this.date_received = date_received;
//	}
//	public String getDate_verified() {
//		return date_verified;
//	}
//	public void setDate_verified(String date_verified) {
//		this.date_verified = date_verified;
//	}
	
}
