package com.bsms.restobjclient.cardotp;

import java.io.Serializable;
import java.util.List;

import com.bsms.restobj.MbApiContentResp;
import com.bsms.restobjclient.emoney.ListContentdoPayment;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class ContentCardOTPdoPayment implements Serializable, MbApiContentResp {
	private String no;
	private String date;
	private String title;
	private String footer;
	private List<ListContentdoPayment> content;
	
	
}
