package com.bsms.restobj;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MbApiReq implements Serializable {

	private static final long serialVersionUID = 6798772440711883353L;

	@JsonProperty("pin_offset")
	private String pinOffset;
	@JsonProperty("card_number")
	private String cardNumber;
	@JsonProperty("session_id")
	private String sessionId;

	private Integer customerLimitType;
	private String reqTime;
	private String reffNum;
	private String clientId;
	private String systemId;
	private String typeReq;
	private String channel;
	private String billingNo;
	private String billingAmount;
	private String denom;
	private String cifNum;
	private String branchId;
	private String terminalId;
	private String tellerId;
	private String startDate;
	private String endDate;
	private String traceNum;
	private String channelType;
	private String channelId;
	private String coreUsername;
	private String corePassword;
	private String coreCompany;
	private String colomName;
	private String idAccount;
	private String operand;
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
	private String username;
	private String password;
	private String numberOfTransaction;
	private String correlationId;
	private String transactionId;
	private String deliveryChannel;
	private String sourceAccountNumber;
	private String sourceAccountName;
	private String destinationAccountNumber;
	private String destinationAccountName;
	private String encryptedPinBlock;
	private String amount;
	private String description;
	private String stan;
	private String pan;
	private String caId;
	private String zpkLMK;
	private String msisdn;
	private String customerName;
	private String customerEmail;
	private String notifType;
	private String token;
	private String activationCode;
	private String otp;
	private String zpk;
	private String modulId;
	private String versionName; 
	private String ipAddress; 
	private String requestType; 
	private String versionValue; 
	
	private String modul_id;
	private String public_key;
	private String ip_address;
	private String version_name;
	private String request_type;
	private String version_value;
	private String device_type;
	private String account_number;

	private Object content;
	private MbApiContentReq reqContent;
	private MbApiContentReq reqData;

}
