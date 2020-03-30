package com.bsms.restobjclient;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class ActivationReq implements Serializable {

	private static final long serialVersionUID = -4380749865668286563L;
	
	@JsonProperty("activation_code")
	private String activationCode;
	private String device;
	@JsonProperty("device_type")
	private String deviceType;
	private String imei;
	@JsonProperty("ip_address")
	private String ipAddress;
	private String msisdn;
	@JsonProperty("os_type")
	private String osType;
	@JsonProperty("os_version")
	private String osVersion;
	private String otp;
	@JsonProperty("publicKey")
	private String public_key;
	@JsonProperty("requestType")
	private String request_type;
	@JsonProperty("version_name")
	private String versionName;
	@JsonProperty("version_value")
	private String versionValue;
	
}
