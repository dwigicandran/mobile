package com.bsms.except;

import com.bsms.restobj.MbApiStatusResp;

public class MbServiceException extends RuntimeException {

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
	
}
