package com.bsms.restobjclient;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentReq;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceInquiryReq implements Serializable {

	private static final long serialVersionUID = 7699079508466417400L;

	private String coreUsername;
	private String corePassword;
	private String coreCompany;
	private String colomName;
	private String operand;
	
	private String ipAddress;
	private String versionName;
	private String requestType;
	private String idAccount;
	private String versionValue;
	private String deviceType;
	private String imei;
	private String language;
	private String menuId;
	private String pin;
	private String customerId;
	private String osVersion;
	private String device;
	private String dateLocal;
	private String osType;
	private String accountNumber;
	
	
}
