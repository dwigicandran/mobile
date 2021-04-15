package com.bsms.restobjclient.transfer;

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
public class BankDispResp implements MbApiContentResp, Serializable {
	
	private static final long serialVersionUID = 5074717308798811211L;
	
	private List<Bank> content=null;
	
	public BankDispResp(List<Bank> bank) {
				
				this.content=bank;
				
	}
	
}
