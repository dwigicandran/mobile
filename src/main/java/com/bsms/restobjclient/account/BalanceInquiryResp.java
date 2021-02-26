package com.bsms.restobjclient.account;

import java.io.Serializable;
import java.util.List;

import com.bsms.restobj.MbApiContentResp;
import com.dto.balanceinfo.Content;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@Getter
@Setter
public class BalanceInquiryResp implements Serializable, MbApiContentResp {

	private String timestamp;
	private String status;
	private String error;
	private String message;
	
	private String responseCode;
	private String correlationId;
	private String transactionId;
	private Content content; 
	
}
