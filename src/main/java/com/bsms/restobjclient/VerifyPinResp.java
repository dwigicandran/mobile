package com.bsms.restobjclient;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VerifyPinResp {

	private String response;
	@JsonProperty("transaction_id")
	private String transactionId;
	@JsonProperty("response_code")
	private String responseCode;
	
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	
	
}
