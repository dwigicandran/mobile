package com.bsms.restobjclient;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifyPinReq {
	
	private String device;
	@JsonProperty("device_type")
	private String deviceType;
	private String imei;
	@JsonProperty("ip_address")
	private String ipAddress;
	@JsonProperty("os_type")
	private String osType;
	@JsonProperty("os_version")
	private String osVersion;
	private String pin;
	@JsonProperty("request_type")
	private String requestType;
	@JsonProperty("version_name")
	private String versionName;
	@JsonProperty("version_value")
	private String versionValue;
	private String card_number;
	private String pin_offset;
	private String zpk;
	private String modulId;
	private String srcAcc;
	private String note;
	private String amount;
	private String benfAcc;
	private String sessionId;
	
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getOsType() {
		return osType;
	}
	public void setOsType(String osType) {
		this.osType = osType;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public String getPin() {
		return pin;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getVersionValue() {
		return versionValue;
	}
	public void setVersionValue(String versionValue) {
		this.versionValue = versionValue;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getZpk() {
		return zpk;
	}
	public void setZpk(String zpk) {
		this.zpk = zpk;
	}
	public String getModulId() {
		return modulId;
	}
	public void setModulId(String modulId) {
		this.modulId = modulId;
	}
	public String getSrcAcc() {
		return srcAcc;
	}
	public void setSrcAcc(String srcAcc) {
		this.srcAcc = srcAcc;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getBenfAcc() {
		return benfAcc;
	}
	public void setBenfAcc(String benfAcc) {
		this.benfAcc = benfAcc;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getCard_number() {
		return card_number;
	}
	public void setCard_number(String card_number) {
		this.card_number = card_number;
	}
	public String getPin_offset() {
		return pin_offset;
	}
	public void setPin_offset(String pin_offset) {
		this.pin_offset = pin_offset;
	}
	
}
