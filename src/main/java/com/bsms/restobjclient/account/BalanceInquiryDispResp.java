package com.bsms.restobjclient.account;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.bsms.restobj.MbApiResp;
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
public class BalanceInquiryDispResp implements Serializable, MbApiContentResp {

	private static final long serialVersionUID = 3246290853981091928L;
	
	private String responseCode;
	private String correlationId;
	private String transactionId;
	private String accountNumber;
	private String amount;
	private String response;
	private String share;
	private String currency;
	
	private String errorCode;
	private String errorMessage;
	
	private Content content;
	
	
}
