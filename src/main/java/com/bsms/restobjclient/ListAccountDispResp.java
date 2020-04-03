package com.bsms.restobjclient;

import java.io.Serializable;
import java.util.List;

import com.bsms.restobj.MbApiContentResp;
import com.dto.accountlist.Allacc;
import com.dto.accountlist.FinTrxAccount;
import com.dto.accountlist.ListOfAccount;
import com.dto.accountlist.Specfintrxacc;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class ListAccountDispResp implements MbApiContentResp, Serializable {

	private String responseCode;
	private String correlationId;
	private String transactionId;
	private String bcust;
	private String custdisp;
	private String menulist;
	private String specmenulist;

	private List<ListOfAccount> listOfAccount;

//	private List<String> fintrxacc;
//	private List<String> specfintrxacc;
//	private List<String> allacc;

	private List<FinTrxAccount> finTrxAccount;
	private List<Allacc> allacc;
	private List<Specfintrxacc> specfintrxacc;

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getBcust() {
		return bcust;
	}

	public void setBcust(String bcust) {
		this.bcust = bcust;
	}

	public String getCustdisp() {
		return custdisp;
	}

	public void setCustdisp(String custdisp) {
		this.custdisp = custdisp;
	}

	public List<ListOfAccount> getListOfAccount() {
		return listOfAccount;
	}

	public void setListOfAccount(List<ListOfAccount> list) {
		this.listOfAccount = list;
	}

	public String getMenulist() {
		return menulist;
	}

	public void setMenulist(String menulist) {
		this.menulist = menulist;
	}

	public String getSpecmenulist() {
		return specmenulist;
	}

	public void setSpecmenulist(String specmenulist) {
		this.specmenulist = specmenulist;
	}

//	public List<String> getFintrxacc() {
//		return fintrxacc;
//	}
//
//	public void setFintrxacc(List<String> fintrxacc) {
//		this.fintrxacc = fintrxacc;
//	}

	public List<FinTrxAccount> getFinTrxAccount() {
		return finTrxAccount;
	}

	public void setFinTrxAccount(List<FinTrxAccount> finTrxAccount) {
		this.finTrxAccount = finTrxAccount;
	}

	public List<Specfintrxacc> getSpecfintrxacc() {
		return specfintrxacc;
	}

	public void setSpecfintrxacc(List<Specfintrxacc> specfintrxaccount) {
		this.specfintrxacc = (List<Specfintrxacc>) specfintrxaccount;
	}

	public List<Allacc> getAllacc() {
		return allacc;
	}

	public void setAllacc(List<Allacc> allaccount) {
		this.allacc = (List<Allacc>) allaccount;
	}


}
