package com.bsms.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import com.bsms.domain.MbApiTxLog;
import com.bsms.except.MbServiceException;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.util.MbErrorUtil;

public class MbBaseServiceImpl {

	protected Validator validator;

    public MbBaseServiceImpl (){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    protected void validate(Object o) {

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate( o );

        MbApiStatusResp[] errors = new MbApiStatusResp[constraintViolations.size()];
        int i=0;
        for (ConstraintViolation<Object> constraintViolation : constraintViolations) {
            errors[i] = new MbApiStatusResp("object.validation.error", constraintViolation.getMessage());
            i++;
        }
        if (i>0){
            throw new MbServiceException(errors);
        }

    }

    protected MbServiceException createSlServiceException(String errCode, String errDesc, MbApiTxLog txLog, MbTxLogRepository txLogRepository) {

        txLogRepository.save(txLog);
        
        MbServiceException response  = new MbServiceException(null);
        response.setResponseCode(errCode);
        response.setResponseMessage(errDesc);

        // return response;
        return new MbServiceException(MbErrorUtil.createError(errCode, errDesc));
    }
	
}
