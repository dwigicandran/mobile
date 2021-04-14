package com.bsms.service.mpn;

import com.bsms.cons.MbConstant;
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
@Service("mpnReInquiry")
public class MpnReinquiry extends MbBaseServiceImpl implements MbService {


    @Value("${mpn.reinquiry}")
    private String mpnUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp mbApiResp;

        log.info("MPN ReInquiry Request : " + new Gson().toJson(request));

        String errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_REQUEST_EN : MbConstant.ERROR_REQUEST_ID;

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = mpnUrl;
            log.info("MPN ReInquiry URL : " + url);

            ResponseEntity<BaseResponse> restResponse = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            log.info("MPN ReInquiry Response : " + new Gson().toJson(restResponse));

            BaseResponse paymentInquiryResp = restResponse.getBody();
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createPDAMResponse(restResponse.getBody(), request.getLanguage());
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

}
