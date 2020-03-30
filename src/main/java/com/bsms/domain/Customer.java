package com.bsms.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import javax.persistence.Id;

@Entity
@Table(name = "Customer")
public class Customer {

	@Id
	private Long id;
	
	private String name;
	private String msisdn;
	private String imei;
	private String activationcode;
	private String registerby;
	private String userlevel;
	private String matmbersama;
	private String createdate;
	private String bsmnetid;
	private String email;
	private String lastaccess;
	private String accesscount;
	private String platform;
	private String version;
	private String tak;
	private String machex;
	private int failedpincount;
	private String createotpdate;
	private String isblockedpermanently;
	private String type;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getActivationcode() {
		return activationcode;
	}
	public void setActivationcode(String activationcode) {
		this.activationcode = activationcode;
	}
	public String getRegisterby() {
		return registerby;
	}
	public void setRegisterby(String registerby) {
		this.registerby = registerby;
	}
	public String getUserlevel() {
		return userlevel;
	}
	public void setUserlevel(String userlevel) {
		this.userlevel = userlevel;
	}
	public String getMatmbersama() {
		return matmbersama;
	}
	public void setMatmbersama(String matmbersama) {
		this.matmbersama = matmbersama;
	}
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public String getBsmnetid() {
		return bsmnetid;
	}
	public void setBsmnetid(String bsmnetid) {
		this.bsmnetid = bsmnetid;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLastaccess() {
		return lastaccess;
	}
	public void setLastaccess(String lastaccess) {
		this.lastaccess = lastaccess;
	}
	public String getAccesscount() {
		return accesscount;
	}
	public void setAccesscount(String accesscount) {
		this.accesscount = accesscount;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getTak() {
		return tak;
	}
	public void setTak(String tak) {
		this.tak = tak;
	}
	public String getMachex() {
		return machex;
	}
	public void setMachex(String machex) {
		this.machex = machex;
	}
	public String getCreateotpdate() {
		return createotpdate;
	}
	public void setCreateotpdate(String createotpdate) {
		this.createotpdate = createotpdate;
	}
	public String getIsblockedpermanently() {
		return isblockedpermanently;
	}
	public void setIsblockedpermanently(String isblockedpermanently) {
		this.isblockedpermanently = isblockedpermanently;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getFailedpincount() {
		return failedpincount;
	}
	public int setFailedpincount(int failedpincount) {
		return this.failedpincount = failedpincount;
	}
	
//	public void setFailedpincount(int failedpincount) {
//		this.failedpincount = failedpincount;
//	}
	
}
