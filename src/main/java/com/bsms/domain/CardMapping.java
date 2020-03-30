package com.bsms.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "cardmapping")
public class CardMapping {

	@Id
	@JsonProperty("ID")
	private Long ID;
	@JsonProperty("AccountNumber")
	private String accountnumber;
	@JsonProperty("CardNumber")
	private String cardnumber;
	@JsonProperty("customerid")
	private Long customerid;
	@JsonProperty("AccountType")
	private String accounttype;
	@JsonProperty("BranchCode")
	private String branchcode;
	@JsonProperty("PinOffset")
	private String pinoffset;
	
	public String getAccountnumber() {
		return accountnumber;
	}
	public void setAccountnumber(String accountnumber) {
		this.accountnumber = accountnumber;
	}
	public String getCardnumber() {
		return cardnumber;
	}
	public void setCardnumber(String cardnumber) {
		this.cardnumber = cardnumber;
	}
	public Long getCustomerid() {
		return customerid;
	}
	public void setCustomerid(Long customerid) {
		this.customerid = customerid;
	}
	public String getAccounttype() {
		return accounttype;
	}
	public void setAccounttype(String accounttype) {
		this.accounttype = accounttype;
	}
	public String getBranchcode() {
		return branchcode;
	}
	public void setBranchcode(String branchcode) {
		this.branchcode = branchcode;
	}
	public String getPinoffset() {
		return pinoffset;
	}
	public void setPinoffset(String pinoffset) {
		this.pinoffset = pinoffset;
	}
	public Long getID() {
		return ID;
	}
	public void setID(Long iD) {
		ID = iD;
	}
	
}
