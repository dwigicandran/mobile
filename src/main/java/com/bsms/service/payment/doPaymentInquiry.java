package com.bsms.service.payment;

import com.bsms.cons.MbApiConstant;
import com.bsms.cons.MbConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

//Addition By Dwi S - September 2020

@Slf4j
@Service("doPaymentInquiry")
public class doPaymentInquiry extends MbBaseServiceImpl implements MbService {

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;

    MbApiResp mbApiResp;

    @Value("${payment.doInquiry}")
    private String doInquiryPayment;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Value("${sql.conf}")
    private String sqlconf;


    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) {

        MbApiTxLog txLog = new MbApiTxLog();

        log.info("::: doInquiry Request RUN :::");
        log.info(new Gson().toJson(request));
        String response_msg = "";

        String errorDefault = "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
        if (request.getLanguage().equals("en")) {
            errorDefault = "Request can't be process, please try again later.";
        }

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            String url = doInquiryPayment;
            log.info("Inquiry URL : " + url);
            RestTemplate restTemplate = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(restTimeout);

            ResponseEntity<BaseResponse> response = restTemplate.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            log.info("::: doInquiry Response :::");
            log.info(new Gson().toJson(response.getBody()));

            BaseResponse paymentInquiryResp = response.getBody();

//            if (response.getBody() != null) {
//                mbApiResp = MbJsonUtil.createResponse(response.getBody());
//            } else {
//                MbLogUtil.writeLogError(log, "Response body null", MbApiConstant.NOT_AVAILABLE);
//                mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
//            }

            if (paymentInquiryResp.getResponseCode().equals("00")) {
                String limitResponse = checkLimit(response.getBody().getAmount(), request.getCustomerLimitType(), request.getMsisdn(), TrxLimit.PAYMENT);

                if (limitResponse.equalsIgnoreCase("01")) {
                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_FINANCIAL_EN : MbConstant.ERROR_LIMIT_FINANCIAL_ID;
                    mbApiResp = MbJsonUtil.createResponseTrf("01", response_msg, null, "");
                } else if (limitResponse.equalsIgnoreCase("02")) {
                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_EXCEED_EN : MbConstant.ERROR_LIMIT_EXCEED_ID;
                    mbApiResp = MbJsonUtil.createResponseTrf("02", response_msg, null, "");
                } else {
                    mbApiResp = MbJsonUtil.createResponse(response.getBody());
                }
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

        } catch (Exception e) {
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
            MbLogUtil.writeLogError(log, "99", e.toString());
        }
        txLog.setId(mbApiResp.getTransactionId());
        txLog.setResponse(mbApiResp);
        txLog.setRequest(request);
        txLogRepository.save(txLog);

        return mbApiResp;
    }


    private String checkLimit(String amount, int customerLimitType, String msisdn, int trxType) {
//        String limitResponseCode = MbConstant.ERROR_NUM_UNKNOWN;
        String limitResponseCode = "00";
        TrxLimit trxLimit = new TrxLimit();
        JSONObject value = new JSONObject();

        try {
            double pdamAmount = Double.parseDouble(amount); //transaction amount
            long amount_convert = (new Double(pdamAmount)).longValue(); //129
            limitResponseCode = trxLimit.checkLimit(msisdn, customerLimitType, trxType, amount_convert, value, sqlconf);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Limit Check Error : " + e.getMessage());
        }

        return limitResponseCode;
    }


}
