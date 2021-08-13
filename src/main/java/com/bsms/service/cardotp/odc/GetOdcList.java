package com.bsms.service.cardotp.odc;

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
@Service("getOdcList")
public class GetOdcList extends MbBaseServiceImpl implements MbService {

    @Value("${cardotp.odc.getList}")
    private String getListUrl;

    @Value("${cardotp.odc.getListActive}")
    private String getListUrlActive;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Value("${sql.conf}")
    private String sqlconf;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp mbApiResp;

        log.info("Get CardOtp Odc List Request : " + new Gson().toJson(request));

        String errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_REQUEST_EN : MbConstant.ERROR_REQUEST_ID;

        try {

            String requestPath = requestContext.getUriInfo().getPath();
            String requestParam = requestPath.substring(requestPath.lastIndexOf('/') + 1);
            String url = null;

            if("active".equals(requestParam)) {
                url = getListUrlActive;
            } else {
                url = getListUrl;
            }

            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);

            log.info("Get CardOtp Odc List URL : " + url);
            ResponseEntity<BaseResponse> restResponse = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            log.info("Get CardOtp Odc List Response : " + new Gson().toJson(restResponse));

            BaseResponse paymentInquiryResp = restResponse.getBody();
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(restResponse.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(restResponse.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e.getCause().getClass().getName().equalsIgnoreCase("java.net.SocketTimeoutException")) {
                errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_TIMEOUT_REQUEST_EN : MbConstant.ERROR_TIMEOUT_REQUEST_ID; //time out exception
            }
            mbApiResp = MbJsonUtil.createResponseBank(MbConstant.ERROR_NUM_UNKNOWN, errorDefault, null);
        }
        return mbApiResp;
    }

}