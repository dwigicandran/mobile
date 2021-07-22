package com.bsms.service.cardotp.odc;

import com.bsms.cons.MbConstant;
import com.bsms.repository.MbLimitRepository;
import com.bsms.repository.MbLimitTrackingRepository;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

@Slf4j
@Service("doOdcInquiry")
public class OdcInquiry extends MbBaseServiceImpl implements MbService {

    @Value("${cardotp.odc.inquiry}")
    private String odcInquiryUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Value("${sql.conf}")
    private String sqlconf;
    
    @Autowired MbLimitRepository limitRepository;
    @Autowired MbLimitTrackingRepository limitTrackingRepository;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp mbApiResp;

        log.info("Get CardOtp Odc Inquiry Request : " + new Gson().toJson(request));

        String errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_REQUEST_EN : MbConstant.ERROR_REQUEST_ID;

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = odcInquiryUrl;
            String response_msg;

            log.info("Get CardOtp Odc Inquiry Add URL : " + url);
            
            long amt = Long.valueOf(request.getAmount());
            Integer custType = request.getCustomerLimitType();
            BigDecimal trxAmount = new BigDecimal(request.getAmount());

            String limitResponse = checklimitTransaction(request.getAmount(), request.getCustomerLimitType(), request.getMsisdn(), TrxLimit.PURCHASE);

//            String limitResponse = TrxLimit.checkTransLimit(request.getAmount(), request.getCustomerLimitType(), request.getMsisdn(), TrxLimit.PURCHASE, sqlconf);
            System.out.println("Limit Response : " + limitResponse);

            if (limitResponse.equalsIgnoreCase("01")) {
                response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_FINANCIAL_EN : MbConstant.ERROR_LIMIT_FINANCIAL_ID;
                mbApiResp = MbJsonUtil.createResponseTrf("99", response_msg, null, "");
            } else if (limitResponse.equalsIgnoreCase("02")) {
                response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_FINANCIAL_EN : MbConstant.ERROR_LIMIT_FINANCIAL_ID;
                mbApiResp = MbJsonUtil.createResponseTrf("99", response_msg, null, "");
            } else {
                ResponseEntity<BaseResponse> restResponse = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
                log.info("Get CardOtp Odc Inquiry Response : " + new Gson().toJson(restResponse));
                BaseResponse paymentInquiryResp = restResponse.getBody();
                if (paymentInquiryResp.getResponseCode().equals("00")) {
                    mbApiResp = MbJsonUtil.createResponse(restResponse.getBody());
                } else {
                    mbApiResp = MbJsonUtil.createErrResponse(restResponse.getBody());
                }
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
}
