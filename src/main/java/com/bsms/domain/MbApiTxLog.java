package com.bsms.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.util.MbDateFormatUtil;

@Document(collection = "mb_tx_log")
public class MbApiTxLog {

	@Id
	private String id;
	private MbApiReq request;
	private MbApiResp response;
	private String txDate;
	
	public MbApiTxLog() {
		
	}
	
	public MbApiTxLog(MbApiReq request) {
		this.request = request;
		this.txDate = MbDateFormatUtil.formatDate(new Date());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MbApiReq getRequest() {
		return request;
	}

	public void setRequest(MbApiReq request) {
		this.request = request;
	}

	public MbApiResp getResponse() {
		return response;
	}

	public void setResponse(MbApiResp response) {
		this.response = response;
	}

	public String getTxDate() {
		return txDate;
	}

	public void setTxDate(String txDate) {
		this.txDate = txDate;
	}
	
}
