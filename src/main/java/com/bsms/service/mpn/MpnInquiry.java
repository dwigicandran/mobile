package com.bsms.service.mpn;

import com.bsms.cons.MbConstant;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

@Slf4j
@Service("mpnInquiry")
public class MpnInquiry extends MbBaseServiceImpl implements MbService {

    @Value("${mpn.inquiry}")
    private String mpnUrl;

    @Value("${mpn.reinquiry}")
    private String mpnReinquiryUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Value("${sql.conf}")
    private String sqlconf;

    @Autowired
    private ApplicationContext context;

    @Context
    private ContainerRequestContext requestContext;


    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp mbApiResp;

        log.info("MPN Inquiry Request : " + new Gson().toJson(request));
        String endpointUrl = "";
        String response_msg = null;
        String errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_REQUEST_EN : MbConstant.ERROR_REQUEST_ID;

        if (request.getBillerid().equalsIgnoreCase("90005")) {
            mbApiResp = mpnInquiry(request);
        } else {
            mbApiResp = mpnReinquiry(header, requestContext, request);
        }

//        try {
//            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
//            RestTemplate restTemps = new RestTemplate();
//            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
//            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
//            String url = endpointUrl;
//
//            log.info("MPN Inquiry URL : " + url);
//
//            ResponseEntity<BaseResponse> restResponse = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
//
//            log.info("MPN Inquiry Response : " + new Gson().toJson(restResponse));
//            log.info("MPN AMOUNT : " + restResponse.getBody().getAmount());
//
//            BaseResponse paymentInquiryResp = restResponse.getBody();
//            if (paymentInquiryResp.getResponseCode().equals("00")) {
//                String limitResponse = checkLimit(restResponse.getBody().getAmount(), request.getCustomerLimitType(), request.getMsisdn(), TrxLimit.PAYMENT);
//
//                if (limitResponse.equalsIgnoreCase("01")) {
//                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_FINANCIAL_EN : MbConstant.ERROR_LIMIT_FINANCIAL_ID;
//                    mbApiResp = MbJsonUtil.createResponseTrf("01", response_msg, null, "");
//                } else if (limitResponse.equalsIgnoreCase("02")) {
//                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_EXCEED_EN : MbConstant.ERROR_LIMIT_EXCEED_ID;
//                    mbApiResp = MbJsonUtil.createResponseTrf("02", response_msg, null, "");
//                } else {
//                    mbApiResp = MbJsonUtil.createPDAMResponse(restResponse.getBody(), request.getLanguage());
//                }
//            } else {
//                mbApiResp = MbJsonUtil.createErrResponse(restResponse.getBody());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (e.getCause().getClass().getName().equalsIgnoreCase("java.net.SocketTimeoutException")) {
//                //time out exception
//                errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_TIMEOUT_REQUEST_EN : MbConstant.ERROR_TIMEOUT_REQUEST_ID;
//            }
//            mbApiResp = MbJsonUtil.createResponseBank(MbConstant.ERROR_NUM_UNKNOWN, errorDefault, null);
//        }
        return mbApiResp;
    }


    private MbApiResp mpnInquiry(MbApiReq request) {
        String response_msg = null;
        MbApiResp mbApiResp;
        String errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_REQUEST_EN : MbConstant.ERROR_REQUEST_ID;

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = mpnUrl;

            log.info("MPN Inquiry URL : " + url);

            ResponseEntity<BaseResponse> restResponse = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            log.info("MPN Inquiry Response : " + new Gson().toJson(restResponse));
            log.info("MPN AMOUNT : " + restResponse.getBody().getAmount());

            BaseResponse paymentInquiryResp = restResponse.getBody();
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                String limitResponse = checkLimit(restResponse.getBody().getAmount(), request.getCustomerLimitType(), request.getMsisdn(), TrxLimit.PAYMENT);

                if (limitResponse.equalsIgnoreCase("01")) {
                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_FINANCIAL_EN : MbConstant.ERROR_LIMIT_FINANCIAL_ID;
                    mbApiResp = MbJsonUtil.createResponseTrf("01", response_msg, null, "");
                } else if (limitResponse.equalsIgnoreCase("02")) {
                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_EXCEED_EN : MbConstant.ERROR_LIMIT_EXCEED_ID;
                    mbApiResp = MbJsonUtil.createResponseTrf("02", response_msg, null, "");
                } else {
                    mbApiResp = MbJsonUtil.createPDAMResponse(restResponse.getBody(), request.getLanguage());
                }
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(restResponse.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getCause().getClass().getName().equalsIgnoreCase("java.net.SocketTimeoutException")) {
                //time out exception
                errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_TIMEOUT_REQUEST_EN : MbConstant.ERROR_TIMEOUT_REQUEST_ID;
            }
            mbApiResp = MbJsonUtil.createResponseBank(MbConstant.ERROR_NUM_UNKNOWN, errorDefault, null);
        }
        return mbApiResp;
    }


    private MbApiResp mpnReinquiry(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp response;
        MbService service = (MbService) context.getBean("mpnReInquiry");
        response = service.process(header, requestContext, request);
        return response;
    }

    private String checkLimit(String amount, int customerLimitType, String msisdn, int trxType) {
        String limitResponseCode = MbConstant.ERROR_NUM_UNKNOWN;
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
