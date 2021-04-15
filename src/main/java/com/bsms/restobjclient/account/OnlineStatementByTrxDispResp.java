package com.bsms.restobjclient.account;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.dto.onlinestatementbytrx.Content;
import com.dto.onlinestatementbytrx.DetailTransaksi;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
public class OnlineStatementByTrxDispResp implements MbApiContentResp, Serializable {
	
	private String responseCode;
	private String transactionId;
	private String periode;
	private String yHEAD1FIX;
	private String accountName;
	private String custAdd;
	private String custAdd2;
	private String custAdd3;
	private String saldoAwal;
	private String totalDebet;
	private String totalKredit;
	private String saldoAkhir;
	
	private DetailTransaksi[] detailTransaksi;
	
	private Content content;

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getPeriode() {
		return periode;
	}

	public void setPeriode(String periode) {
		this.periode = periode;
	}

	public String getyHEAD1FIX() {
		return yHEAD1FIX;
	}

	public void setyHEAD1FIX(String yHEAD1FIX) {
		this.yHEAD1FIX = yHEAD1FIX;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCustAdd() {
		return custAdd;
	}

	public void setCustAdd(String custAdd) {
		this.custAdd = custAdd;
	}

	public String getCustAdd2() {
		return custAdd2;
	}

	public void setCustAdd2(String custAdd2) {
		this.custAdd2 = custAdd2;
	}

	public String getCustAdd3() {
		return custAdd3;
	}

	public void setCustAdd3(String custAdd3) {
		this.custAdd3 = custAdd3;
	}

	public String getSaldoAwal() {
		return saldoAwal;
	}

	public void setSaldoAwal(String saldoAwal) {
		this.saldoAwal = saldoAwal;
	}

	public String getTotalDebet() {
		return totalDebet;
	}

	public void setTotalDebet(String totalDebet) {
		this.totalDebet = totalDebet;
	}

	public String getTotalKredit() {
		return totalKredit;
	}

	public void setTotalKredit(String totalKredit) {
		this.totalKredit = totalKredit;
	}

	public String getSaldoAkhir() {
		return saldoAkhir;
	}

	public void setSaldoAkhir(String saldoAkhir) {
		this.saldoAkhir = saldoAkhir;
	}

	public DetailTransaksi[] getDetailTransaksi() {
		return detailTransaksi;
	}

	public void setDetailTransaksi(DetailTransaksi[] detailTransaksi) {
		this.detailTransaksi = detailTransaksi;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}
	
	
}
