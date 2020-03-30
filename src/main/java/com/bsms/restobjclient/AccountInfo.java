package com.bsms.restobjclient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
public class AccountInfo {

	private String accountNumber;
	private String customer;
	private String accountTittle;
	private String accountType;
	private String currency;
	private String coCode;
	private String coCodeName;
	private String workBalance;
	private String lockAmount;
	private String minBalance;
	private String openingDate;
	private String category;
	private String availableBalance;
	
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getAccountTittle() {
		return accountTittle;
	}
	public void setAccountTittle(String accountTittle) {
		this.accountTittle = accountTittle;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getCoCode() {
		return coCode;
	}
	public void setCoCode(String coCode) {
		this.coCode = coCode;
	}
	public String getCoCodeName() {
		return coCodeName;
	}
	public void setCoCodeName(String coCodeName) {
		this.coCodeName = coCodeName;
	}
	public String getWorkBalance() {
		return workBalance;
	}
	public void setWorkBalance(String workBalance) {
		this.workBalance = workBalance;
	}
	public String getLockAmount() {
		return lockAmount;
	}
	public void setLockAmount(String lockAmount) {
		this.lockAmount = lockAmount;
	}
	public String getMinBalance() {
		return minBalance;
	}
	public void setMinBalance(String minBalance) {
		this.minBalance = minBalance;
	}
	public String getOpeningDate() {
		return openingDate;
	}
	public void setOpeningDate(String openingDate) {
		this.openingDate = openingDate;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(String availableBalance) {
		this.availableBalance = availableBalance;
	}
	
}
