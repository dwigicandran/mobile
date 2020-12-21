package com.bsms.service.pdam;

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
@Service("pdamInquiry")
public class PdamInquiry extends MbBaseServiceImpl implements MbService {

    @Value("${pdam.ubp.inquiry}")
    private String pdamUbpUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp mbApiResp;

        log.info("PDAM Inquiry  Runnning");
        log.info("PDAM Inquiry Request : " + new Gson().toJson(request));

//        String requestPath = requestContext.getUriInfo().getPath();
//        String requestParam = requestPath.substring(requestPath.lastIndexOf('/') + 1);
//        String billerId = requestParam != null || !requestParam.equals("") ? requestParam : request.getBillerid() != null ? request.getBillerid() : "";

        log.info("Biller Id " + request.getBillerid());

        try {
            String billerId = request.getBillerid();
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = pdamUbpUrl + "/" + billerId;

            log.info("PDAM Inquiry : " + url);
            ResponseEntity<BaseResponse> restResponse = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            log.info("PDAM Inquiry Response : " + new Gson().toJson(restResponse));

            BaseResponse paymentInquiryResp = restResponse.getBody();
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                log.info("run create pdam response ");
                mbApiResp = MbJsonUtil.createPDAMResponse(restResponse.getBody(), request.getLanguage());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(restResponse.getBody());
            }

        } catch (Exception e) {
            System.out.println("error : " + e.getMessage());
            String errorDefault = MbConstant.ERROR_REQUEST_ID;
            if (request.getLanguage().equals("en")) {
                errorDefault = MbConstant.ERROR_REQUEST_EN;
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }

        return mbApiResp;
    }
}
