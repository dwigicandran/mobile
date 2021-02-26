package com.bsms.restobjclient.cardotp;

import java.io.Serializable;
import java.util.List;

import com.bsms.restobj.MbApiContentResp;
import com.bsms.restobjclient.emoney.ListContentdoInquiry;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class ContentCardOTPdoInquiry implements Serializable, MbApiContentResp {
	private String accountId;
	private String accountName;
	private String paymentId;
	private String paymentName;
	private String info;
	private List<ListContentdoInquiry> content;
	
	
}
