package com.bsms.restobjclient.authentication;

import java.io.Serializable;
import java.util.List;

import com.bsms.restobj.MbApiContentResp;
import com.dto.balanceinfo.Content;
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
public class PINKeyResp implements Serializable, MbApiContentResp {

	private String timestamp;
	private String status;
	private String error;
	private String message;
	@JsonProperty("response_code")
	private String responseCode;
	private String correlationId;
	private String transactionId;
	private Content content; 
	
	private String clearZPK;
	private String response;
	@JsonProperty("zpk_ZMK")
	private String zpkZmk;
	@JsonProperty("zpk_LMK")
	private String zpkLmk;
	
	/*private String clearZPK;
	private String response;*/
	
}
