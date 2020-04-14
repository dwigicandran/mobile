package com.bsms.domain;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Entity
@Table(name = "Customer")
public class Customer {

	@Id
	@Column(name="ID")
	private Long id;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="MSISDN")
	private String msisdn;
	
	@Column(name="imei")
	private String imei;
	
	@Column(name="Activationcode")
	private String activationcode;
	
	@Column(name="registerby")
	private String registerby;
	
	@Column(name="userlevel")
	private String userlevel;
	
	@Column(name="mATMBersama")
	private String matmbersama;
	
//	@Column(name="createdate")
//	private String createdate;
	
//	@Column(name="BSMNetID")
//	private String bsmnetid;
	
	@Column(name="email")
	private String email;
	
	@Column(name="lastaccess")
	private String lastaccess;
	
//	@Column(name="accesscount")
//	private String accesscount;
	
	@Column(name="Platform")
	private String platform;
	
	@Column(name="Version")
	private String version;
	
	@Column(name="TAK")
	private String tak;
	
	@Column(name="MACHEX")
	private String machex;
	
	@Column(name="failedPINCount")
	private int failedpincount;
	
	@Column(name="createotpdate")
	private String createotpdate;
	
	@Column(name="isblockedpermanently")
	private String isblockedpermanently;
	
	@Column(name="type")
	private String type;

	
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getMsisdn() {
//		return msisdn;
//	}
//	public void setMsisdn(String msisdn) {
//		this.msisdn = msisdn;
//	}
//	public String getImei() {
//		return imei;
//	}
//	public void setImei(String imei) {
//		this.imei = imei;
//	}
//	public String getActivationcode() {
//		return activationcode;
//	}
//	public void setActivationcode(String activationcode) {
//		this.activationcode = activationcode;
//	}

	
	
	
	
}
