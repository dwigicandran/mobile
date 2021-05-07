package com.bsms.restobjclient.cardotp;

import java.util.List;

import com.bsms.domain.OtpPeriod;
import com.bsms.restobj.MbApiContentResp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListOfPeriodValidity implements MbApiContentResp {
	
	public List<OtpPeriod> content;

}
