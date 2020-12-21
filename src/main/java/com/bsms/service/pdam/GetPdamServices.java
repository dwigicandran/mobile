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
@Service("listPdamServices")
public class GetPdamServices extends MbBaseServiceImpl implements MbService {

    @Value("${pdam.ubp.getServices}")
    private String servicesUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {

        MbApiResp mbApiResp;

        log.info("Get Service PDAM Running");
        log.info("Request : " + new Gson().toJson(request));

        String requestPath = requestContext.getUriInfo().getPath();
        String billerId = requestPath.substring(requestPath.lastIndexOf('/') + 1);

        try {
            request.setBillerid(billerId);
            log.info("request :" + new Gson().toJson(request));
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = servicesUrl;

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            BaseResponse paymentInquiryResp = response.getBody();

            log.info("Get PDAM Service Url : " + url);
            log.info("Get PDAM Service Response : " + new Gson().toJson(response));

            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

            log.info("RESPONSE : " + new Gson().toJson(mbApiResp));
        } catch (Exception e) {

            String errorDefault = MbConstant.ERROR_REQUEST_ID;
            if (request.getLanguage().equals("en")) {
                errorDefault = MbConstant.ERROR_REQUEST_EN;
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }
        return mbApiResp;

    }
}
