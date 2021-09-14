package com.bsms.service.base;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbAppContent;
import com.bsms.domain.MbLimit;
import com.bsms.domain.MbLimitTracking;
import com.bsms.except.CustomException;
import com.bsms.except.MbServiceException;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbLimitRepository;
import com.bsms.repository.MbLimitTrackingRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.util.MbErrorUtil;
import com.bsms.util.MbJsonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MbBaseServiceImpl {

    protected Validator validator;

    @Autowired
    MbLimitRepository limitRepository;
    @Autowired
    MbLimitTrackingRepository limitTrackingRepository;
    @Autowired
    MbAppContentRepository mbAppContentRepository;
    
    @Value("${msg.limitexceed.id}") private String limitExceedId;
    @Value("${msg.limitexceed.en}") private String limitExceedEn;
    @Value("${msg.limitfinancial.id}") private String limitFinancialId;
    @Value("${msg.limitfinancial.en}") private String limitFinancialEn;

    public MbBaseServiceImpl (){
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    protected void validate(Object o) {

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(o);

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
    
    protected String checklimitTransaction(String amount, int customerLimitType, String msisdn, int trxType, String language) {
        BigDecimal trxAmount = new BigDecimal(amount);
        String result;
        String response_msg = null;
        Optional<MbLimit> mbLimit = limitRepository.findByCustomerTypeAndTrxTypeAndEnabled(customerLimitType, trxType, "1");
        if(mbLimit.isPresent()) {
            BigDecimal trxAmtLimit = new BigDecimal(mbLimit.get().getTrxAmountLimit());
            BigDecimal dailyAmtLimit = new BigDecimal(mbLimit.get().getDailyAmountLimit());
            Optional<MbLimitTracking> mbLimitTracking = limitTrackingRepository.findByMsisdnAndTrxType(msisdn, trxType);
            if(mbLimitTracking.isPresent()){

                result = "00";

                Calendar calTrxDate = Calendar.getInstance();
                BigDecimal lastAmount = BigDecimal.ZERO;

                Calendar calLastTrxDate = Calendar.getInstance();
                calLastTrxDate.setTime((Date) mbLimitTracking.get().getLastTrxDate());
                if (calLastTrxDate.get(Calendar.DATE) == calTrxDate.get(Calendar.DATE) &&
                        calLastTrxDate.get(Calendar.MONTH) == calTrxDate.get(Calendar.MONTH) &&
                        calLastTrxDate.get(Calendar.YEAR) == calTrxDate.get(Calendar.YEAR)) {
                    lastAmount = new BigDecimal(mbLimitTracking.get().getTotalAmount());
                }

                BigDecimal sum;
                sum = trxAmount.add(lastAmount);

                if (trxAmtLimit.compareTo(BigDecimal.ZERO) > 0) {
                    if (trxAmtLimit.compareTo(trxAmount) == -1) {
                    	response_msg = language.equalsIgnoreCase("en") ? limitExceedEn : limitExceedId;
                    	throw new CustomException(response_msg);
                    }
                }
                
                if (dailyAmtLimit.compareTo(BigDecimal.ZERO) > 0) {
                    if (dailyAmtLimit.compareTo(sum) == -1) {
                    	response_msg = language.equalsIgnoreCase("en") ? limitExceedEn : limitExceedId;
                    	throw new CustomException(response_msg);
                    }
                }
            } else {
                result = "01";
            }
        } else {
        	MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600002", "id");
        	throw new CustomException("99", mbAppContent.getDescription());
        }
        return result;
    }
    
}
