package com.bsms.service.emas.transaction;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbAppContent;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
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

@Service("emasTrxInquiry")
public class EgoldTrxInquiryServiceImpl extends MbBaseServiceImpl implements MbService {

    private static Logger log = LoggerFactory.getLogger(EgoldTrxInquiryServiceImpl.class);

    @Value("${emas.inquiry.purchase}") private String endpointPurchase;
    @Value("${emas.inquiry.sell}") private String endpointSell;
    @Value("${emas.inquiry.trf}") private String endpointTrf;
    @Value("${rest.template.timeout}") private int restTimeout;

    @Autowired
    private MbAppContentRepository mbAppContentRepository;

    private String responseDesc;
    private String responseCode;

    MbApiResp mbApiResp;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {

        String requestPath = requestContext.getUriInfo().getPath();
        String requestParam = requestPath.substring(requestPath.lastIndexOf('/') + 1);
        String url = null;

        if("purchase".equals(requestParam)) {
            url = endpointPurchase;
        } else if("sell".equals(requestParam)) {
            url = endpointSell;
        } else if("trf".equals(requestParam)){
            url = endpointTrf;
        }

        try{
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);

            ResponseEntity<BaseResponse> restResponse = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            BaseResponse paymentInquiryResp = restResponse.getBody();
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(restResponse.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(restResponse.getBody());
            }
        } catch(Exception ex) {
            log.info(ex.toString());

            MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
            responseDesc = mbAppContent.getDescription();
            responseCode = MbApiConstant.ERR_CODE;
            mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
        }
        return mbApiResp;
    }
}
