package com.bsms.restobjclient.ziswaf;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class getListAmilResp implements Serializable, MbApiContentResp {

	private static final long serialVersionUID = -2829802207566341460L;
	
	private String responseCode;
	private String responseMessage;
	private ContentGetListAmil responseContent;
	
	
}
