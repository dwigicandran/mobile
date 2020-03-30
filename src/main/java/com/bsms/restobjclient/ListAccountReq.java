package com.bsms.restobjclient;

import java.io.Serializable;
import java.util.List;

import com.bsms.domain.CardMapping;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
public class ListAccountReq implements Serializable {

	private static final long serialVersionUID = 8432552161319695663L;
	
	private String transactionId;
	private String coreUsername;
	private String corePassword;
	private String coreCompany;
	private String colomName;
	private String operand;
	
	private List<String> listAccountNumber;

	public String getCoreUsername() {
		return coreUsername;
	}

	public void setCoreUsername(String coreUsername) {
		this.coreUsername = coreUsername;
	}

	public String getCorePassword() {
		return corePassword;
	}

	public void setCorePassword(String corePassword) {
		this.corePassword = corePassword;
	}

	public String getCoreCompany() {
		return coreCompany;
	}

	public void setCoreCompany(String coreCompany) {
		this.coreCompany = coreCompany;
	}

	public String getColomName() {
		return colomName;
	}

	public void setColomName(String colomName) {
		this.colomName = colomName;
	}

	public String getOperand() {
		return operand;
	}

	public void setOperand(String operand) {
		this.operand = operand;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public List<String> getListAccountNumber() {
		return listAccountNumber;
	}

	public void setListAccountNumber(List<String> listAccountNumber) {
		this.listAccountNumber = listAccountNumber;
	}

	
}
