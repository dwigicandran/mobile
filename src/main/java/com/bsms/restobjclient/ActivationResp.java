package com.bsms.restobjclient;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class ActivationResp implements Serializable, MbApiContentResp {

	@JsonProperty("customer_id")
	private String customerId;
	private String name;
	private String clearZPK;
	@JsonProperty("transaction_id")
	private String transactionId;
	@JsonProperty("response_code")
	private String responseCode;
	@JsonProperty("public_key")
	private String publicKey;
	@JsonProperty("session_id")
	private String sessionId;
	private String response;
	private String email;
	private String isreactivation;
	@JsonProperty("costumer_id")
	private String costumerId;
	
}
