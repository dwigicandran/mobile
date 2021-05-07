package com.bsms.restobjclient.cardotp;

import java.util.List;

import com.bsms.restobj.MbApiContentResp;

public class ListOfPeriodValidity implements MbApiContentResp {
	
	public List<String> content;

	public List<String> getContent() {
		return content;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}


}
