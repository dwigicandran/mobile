package com.bsms.restobjclient.pinemail;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.bsms.restobjclient.emoney.ContentEmoneydoInquiry;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class doChangeEmailResp implements Serializable, MbApiContentResp {

	private static final long serialVersionUID = -2829802207566341460L;
	
	private String transactionId;
	private String responseCode;
	private String responseMessage;
	private ContentChangePINEmail responseContent;
	
	
}
