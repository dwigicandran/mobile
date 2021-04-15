package com.bsms.restobjclient.account;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class OnlineStatementByTrxReq implements Serializable {

	private String correlationId;
	private String transactionId;
	private String coreUsername;
	private String corePassword;
	private String coreCompany;
	private String colomName;
	private String operand;
	private String idAccount;
	private String numberOfTransaction;
	
}
