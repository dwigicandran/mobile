package com.bsms.service.payment;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.service.transfer.MbInquiryOnlineTrfService;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

//Addition by Dwi S - Agustus 2020

@Service("doPaymentPurchase")
public class doPaymentPurchase extends MbBaseServiceImpl implements MbService {

    @Value("${payment.doPayment}")
    private String doPayment;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Autowired
    private MbTxLogRepository txLogRepository;

    @Value("${sql.conf}")
    private String sqlconf;

    private MbApiResp mbApiResp;
    
    @Value("${template.mail_notif}")
    private String templateMailNotif;

    private static Logger log = LoggerFactory.getLogger(MbInquiryOnlineTrfService.class);

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiTxLog txLog = new MbApiTxLog();
        String errorDefault = "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
        if (request.getLanguage().equals("en")) {
            errorDefault = "Request can't be process, please try again later.";
        }
        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            String url = doPayment;

            log.info("dopayment request : " + new Gson().toJson(req.getBody()));
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);


            log.info("Response From Switcher : \n" + new Gson().toJson(response));

            BaseResponse paymentResp = response.getBody();

            if (paymentResp.getResponseCode().equals("00")) {
                //update transaction limit if response code 00
                if (response.getBody().getResponseCode().equalsIgnoreCase("00")) {
                    try {
                        JSONObject value = new JSONObject();
                        TrxLimit trxLimit = new TrxLimit();
                        String amount = paymentResp.getAmount() != null ? paymentResp.getAmount() : "0";
                        int trxType = TrxLimit.PAYMENT;

                        double d = Double.parseDouble(amount);
                        long amount_convert = (new Double(d)).longValue();
                        trxLimit.LimitUpdate(request.getMsisdn(), request.getCustomerLimitType(), trxType, amount_convert, value, sqlconf);
                       
    	    			
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("Update transaction limit failed :" + e.getMessage());
                    }
                }
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
                LibFunctionUtil.mailNotif(request.getCustomerEmail(),mbApiResp, templateMailNotif, request.getLanguage());
                 
            } else {
                System.out.println("run error");
                mbApiResp = MbJsonUtil.createPdamPaymentErrorResponse(response.getBody(), request.getLanguage());
            }

//            if (response.getBody() != null) {
//                mbApiResp = MbJsonUtil.createResponse(response.getBody());
//            } else {
//                MbLogUtil.writeLogError(log, "Response body null", MbApiConstant.NOT_AVAILABLE);
//                mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
//            }
        } catch (Exception e) {
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
            MbLogUtil.writeLogError(log, "99", e.toString());
        }

        txLog.setResponse(mbApiResp);
        txLog.setRequest(request);
        txLogRepository.save(txLog);

        return mbApiResp;
    }

}