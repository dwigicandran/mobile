package com.bsms.restobjclient;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.dto.internaltransfer.Content;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class PINKeyDispResp implements MbApiContentResp, Serializable {

	private String responseCode;
	private String responseDescription;
	private String correlationId;
	private String transactionId;
	private String respTime;
	private String clearZpk;
	private String response;
	
}
