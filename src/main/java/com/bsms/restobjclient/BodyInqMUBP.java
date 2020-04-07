package com.bsms.restobjclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
public class BodyInqMUBP {

	private String companyCode;
	private String channelID;
	private String customerAccountNumber;
	private String traceNumber;
	private String cardAcceptorTermId;
	private String track2data;
	private String languageCode;
	private String currencyCode;
	private String billKey1;
	
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getChannelID() {
		return channelID;
	}
	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}
	public String getCustomerAccountNumber() {
		return customerAccountNumber;
	}
	public void setCustomerAccountNumber(String customerAccountNumber) {
		this.customerAccountNumber = customerAccountNumber;
	}
	public String getTraceNumber() {
		return traceNumber;
	}
	public void setTraceNumber(String traceNumber) {
		this.traceNumber = traceNumber;
	}
	public String getCardAcceptorTermId() {
		return cardAcceptorTermId;
	}
	public void setCardAcceptorTermId(String cardAcceptorTermId) {
		this.cardAcceptorTermId = cardAcceptorTermId;
	}
	public String getTrack2data() {
		return track2data;
	}
	public void setTrack2data(String track2data) {
		this.track2data = track2data;
	}
	public String getLanguageCode() {
		return languageCode;
	}
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getBillKey1() {
		return billKey1;
	}
	public void setBillKey1(String billKey1) {
		this.billKey1 = billKey1;
	}
	
}
