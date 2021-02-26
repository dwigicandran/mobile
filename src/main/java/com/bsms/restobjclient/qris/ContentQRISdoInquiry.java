package com.bsms.restobjclient.qris;

import java.io.Serializable;
import java.util.List;

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
public class ContentQRISdoInquiry implements Serializable, MbApiContentResp {
	private String mpan;
	private String merchantName;
	private String merchantLocation;
	private String amount;
	private String percentage;
	private String admfee;
	private String tips;
	private String feetype;
	private String remark;
	
	
}
