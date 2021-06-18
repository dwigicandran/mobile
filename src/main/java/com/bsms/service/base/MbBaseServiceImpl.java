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

import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbLimit;
import com.bsms.domain.MbLimitTracking;
import com.bsms.except.MbServiceException;
import com.bsms.repository.MbLimitRepository;
import com.bsms.repository.MbLimitTrackingRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.util.MbErrorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MbBaseServiceImpl {

    protected Validator validator;

    @Autowired
    MbLimitRepository limitRepository;
    @Autowired
    MbLimitTrackingRepository limitTrackingRepository;

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

    protected String checklimitTransaction(String amount, int customerLimitType, String msisdn, int trxType) {
        BigDecimal trxAmount = new BigDecimal(amount);
        String result;
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
                    if (trxAmtLimit.compareTo(trxAmount) == -1)
                        result = "02";
                }

                if (dailyAmtLimit.compareTo(BigDecimal.ZERO) > 0) {
                    if (dailyAmtLimit.compareTo(sum) == -1)
                        result = "02";
                }
            } else {
                result = "01";
            }
        } else {
            result = "99";
        }
        return result;
    }
}
