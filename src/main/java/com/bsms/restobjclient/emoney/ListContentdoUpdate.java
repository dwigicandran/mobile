package com.bsms.restobjclient.emoney;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class ListContentdoUpdate {

	private String percentageFee;
	private String monthlyTop;
	private String cardNumber;
	private String lastBalance;
	private String pendingTopup;
	private String inquiryId;
	private String session;
	
}
