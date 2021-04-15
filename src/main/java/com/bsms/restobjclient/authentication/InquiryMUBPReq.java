package com.bsms.restobjclient.authentication;

import com.dto.inquirymubp.Body;
import com.dto.inquirymubp.SoaHeader;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
public class InquiryMUBPReq {

	private SoaHeader soaHeader;
	private Body body;
	private String customerId;
	
	public SoaHeader getSoaHeader() {
		return soaHeader;
	}
	
	public void setSoaHeader(SoaHeader soaHeader) {
		this.soaHeader = soaHeader;
	}
	
	public Body getBody() {
		return body;
	}
	
	public void setBody(Body body) {
		this.body = body;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
}
