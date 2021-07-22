package com.bsms.restobj;

import java.io.Serializable;

public class MbApiStatusResp implements Serializable {

	private static final long serialVersionUID = -4121666383310888014L;
	
	public static final String RESP_CODE_OK = "Ok";
	public static final String RESP_DESC_OK = "Ok";
	
	private String respCode;
	private String respDesc;
	
	public MbApiStatusResp() {
		
	}
	
	public MbApiStatusResp(String respCode, String respDesc) {
		this.respCode = respCode;
		this.respDesc = respDesc;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespDesc() {
		return respDesc;
	}

	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	
	

}
