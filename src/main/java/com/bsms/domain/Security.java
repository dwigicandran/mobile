package com.bsms.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "Security")
public class Security {

	@Id
	@Column(name = "CustomerID")
	private Long customerId;
	
	@Column(name = "Status")
	private String status;
	
	@Column(name = "changetime")
	private Date ChangeTime;
	
	@Column(name = "ZPK_lmk")
	private String zpkLmk;
	
	@Column(name = "privatekey")
	private String privateKey;
	
	@Column(name = "mb_publickey")
	private String mb_PublicKey;
	
	@Column(name = "mb_device")
	private String mbDevice;
	
	@Column(name = "mb_ip_address")
	private String mbIpAddress;
	
	@Column(name = "mb_imei")
	private String mbImei;
	
	@Column(name = "mb_device_type")
	private String mbDeviceType;
	
	@Column(name = "mb_session_id")
	private String mbSessionId;
	
	@Column(name = "mb_iccid")
	private String mbIccid;
	
	@Column(name = "mb_token")
	private String mbToken;
	
	public Long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getZpkLmk() {
		return zpkLmk;
	}
	public void setZpkLmk(String zpkLmk) {
		this.zpkLmk = zpkLmk;
	}
	public String getMbDevice() {
		return mbDevice;
	}
	public void setMbDevice(String mbDevice) {
		this.mbDevice = mbDevice;
	}
	public String getMbIpAddress() {
		return mbIpAddress;
	}
	public void setMbIpAddress(String mbIpAddress) {
		this.mbIpAddress = mbIpAddress;
	}
	public String getMbImei() {
		return mbImei;
	}
	public void setMbImei(String mbImei) {
		this.mbImei = mbImei;
	}
	public String getMbDeviceType() {
		return mbDeviceType;
	}
	public void setMbDeviceType(String mbDeviceType) {
		this.mbDeviceType = mbDeviceType;
	}
	public String getMbSessionId() {
		return mbSessionId;
	}
	public void setMbSessionId(String mbSessionId) {
		this.mbSessionId = mbSessionId;
	}
	public String getMbToken() {
		return mbToken;
	}
	public void setMbToken(String mbToken) {
		this.mbToken = mbToken;
	}
	public Date getChangeTime() {
		return ChangeTime;
	}
	public void setChangeTime(Date changeTime) {
		ChangeTime = changeTime;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}
	public String getMb_PublicKey() {
		return mb_PublicKey;
	}
	public void setMb_PublicKey(String mb_PublicKey) {
		this.mb_PublicKey = mb_PublicKey;
	}
	public String getMbIccid() {
		return mbIccid;
	}
	public void setMbIccid(String mbIccid) {
		this.mbIccid = mbIccid;
	}
}
