package com.bsms.restobjclient.cardotp;

import java.util.List;

import com.bsms.domain.OtpPeriod;
import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
public class ListOfPeriodValidity implements MbApiContentResp {
	
	public List<OtpPeriod> content;

}
