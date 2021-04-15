package com.bsms.service.pdam;

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
@Service("pdamConfirm")
public class PdamConfirm extends MbBaseServiceImpl implements MbService {

    @Value("${pdam.confirm}")
    private String pdamUbpConfUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Value("${sql.conf}")
    private String sqlconf;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp mbApiResp;
        log.info("PDAM Confirm Running");
        log.info("PDAM Confirm Request : " + new Gson().toJson(request));
        String response_msg;

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = pdamUbpConfUrl;
            log.info("PDAM Confirm Url : " + url);

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            BaseResponse paymentInquiryResp = response.getBody();

            if (paymentInquiryResp.getResponseCode().equals("00")) {

                String limitResponse = TrxLimit.checkTransLimit(paymentInquiryResp.getAmount(), request.getCustomerLimitType(), request.getMsisdn(), TrxLimit.PAYMENT, sqlconf);
                System.out.println("Limit Response : " + limitResponse);

                if (limitResponse.equalsIgnoreCase("01")) {
                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_FINANCIAL_EN : MbConstant.ERROR_LIMIT_FINANCIAL_ID;
                    mbApiResp = MbJsonUtil.createResponseTrf("99", response_msg, null, "");
                } else if (limitResponse.equalsIgnoreCase("02")) {
                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_FINANCIAL_EN : MbConstant.ERROR_LIMIT_FINANCIAL_ID;
                    mbApiResp = MbJsonUtil.createResponseTrf("99", response_msg, null, "");
                } else {
                    mbApiResp = MbJsonUtil.createResponse(response.getBody());
                }

            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

            log.info("PDAM Confirm Response : " + new Gson().toJson(mbApiResp));
        } catch (Exception e) {
            String errorDefault = "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = "Request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
            log.info("Error : " + e.getCause().getMessage());
        }

        return mbApiResp;
    }


}
