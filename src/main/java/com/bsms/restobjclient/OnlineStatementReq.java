package com.bsms.restobjclient;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class OnlineStatementReq implements Serializable {

	private static final long serialVersionUID = -6864000471872941628L;

	private String coreUsername;
	private String corePassword;
	private String coreCompany;
	private String colomName;
	private String operand;
	private String idAccount;
	private String startDate;
	private String endDate;
	private String accountNumber;
	private String correlationId;
	private String transactionId;
	
}
