package com.bsms.restobjclient.transfer;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.dto.internaltransfer.Content;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class InternalTrfResp implements Serializable, MbApiContentResp {

	private static final long serialVersionUID = 4164245547044293391L;
	
	private String responseCode;
	private String correlationId;
	private String transactionId;
	private Content content;
	
}
