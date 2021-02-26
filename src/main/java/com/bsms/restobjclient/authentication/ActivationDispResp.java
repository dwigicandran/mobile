package com.bsms.restobjclient.authentication;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.dto.balanceinfo.Content;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class ActivationDispResp implements Serializable, MbApiContentResp {

	private String customerId;
	private String name;
	private String clearZpk;
	private String transactionId;
	private String responseCode;
	private String publicKey;
	private String sessionId;
	private String response;
	private String email;
	private String isReactivation;
	private String msisdn;
	
}
