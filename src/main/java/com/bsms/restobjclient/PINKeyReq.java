package com.bsms.restobjclient;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentReq;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class PINKeyReq implements Serializable {

	private static final long serialVersionUID = 7699079508466417400L;
	
	@JsonProperty("customer_id")
	private String customerId;
	@JsonProperty("date_local")
	private String dateLocal;
	private String device;
	@JsonProperty("device_type")
	private String deviceType;
	private String imei;
	@JsonProperty("ip_address")
	private String ipAddress;
	@JsonProperty("notif_type")
	private String notifType;
	@JsonProperty("os_type")
	private String osType;
	@JsonProperty("os_version")
	private String osVersion;
	@JsonProperty("request_type")
	private String requestType;
	private String token;
	@JsonProperty("version_name")
	private String versionName;
	@JsonProperty("version_value")
	private String versionValue;
	@JsonProperty("session_id")
	private String sessionId;
	
	private String msisdn;
}
