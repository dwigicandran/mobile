package com.bsms.except;

import com.bsms.restobj.MbApiStatusResp;

public class MbServiceException extends RuntimeException {

	private String responseCode;
	private String responseMessage;
	private MbApiStatusResp[] errors;

    public MbServiceException(MbApiStatusResp[] errors) {
        super();
        this.errors = errors;
    }

    public MbApiStatusResp[] getErrors() {
        return errors;
    }

    public void setErrors(MbApiStatusResp[] errors) {
        this.errors = errors;
    }

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}
	
}
