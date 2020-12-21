package com.bsms.service.pdam;

import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

@Slf4j
@Service("pdamPayment")
public class PdamPayment extends MbBaseServiceImpl implements MbService {

    @Value("${pdam.ubp.payment}")
    private String pdamUbpPaymentUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp mbApiResp;
        log.info("PDAM Payment Running");
        log.info("PDAM Payment Request : " + new Gson().toJson(request));

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = pdamUbpPaymentUrl;
            log.info("PDAM Payment Url : " + url);

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            BaseResponse paymentInquiryResp = response.getBody();

            System.out.println("Payment Response : " + new Gson().toJson(paymentInquiryResp));

            if (paymentInquiryResp.getResponseCode().equals("00")) {
                System.out.println("run create pdam response ");
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                System.out.println("run error");
                mbApiResp = MbJsonUtil.createPdamPaymentErrorResponse(response.getBody(), request.getLanguage());
            }

            log.info("PDAM Payment Response : " + new Gson().toJson(mbApiResp));
        } catch (Exception e) {
            String errorDefault = e.getCause().getMessage() + ", permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + ", request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
            log.info("Error : " + e.getCause().getMessage());
        }

        return mbApiResp;
    }
}
