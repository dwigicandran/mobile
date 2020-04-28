package com.bsms.restobjclient;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class CreatePinRespDisp implements Serializable, MbApiContentResp {
	
	private String message;
	private String transactionId;
	
}
