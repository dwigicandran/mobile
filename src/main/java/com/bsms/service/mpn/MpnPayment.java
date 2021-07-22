package com.bsms.service.mpn;

import com.bsms.cons.MbConstant;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.LibFunctionUtil;
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
@Service("mpnPayment")
public class MpnPayment extends MbBaseServiceImpl implements MbService {

    @Value("${mpn.payment}")
    private String mpnUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Value("${sql.conf}")
    private String sqlconf;
    
    @Value("${template.mail_notif}")
    private String templateMailNotif;


    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp mbApiResp = null;
        log.info("MPN Payment Running");
        log.info("MPN Payment Request : " + new Gson().toJson(request));

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = mpnUrl;
            log.info("MPN Payment Url : " + url);

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            BaseResponse paymentResp = response.getBody();

            log.info("MPN Response : " + new Gson().toJson(paymentResp));

            if (paymentResp.getResponseCode().equals("00")) {
                //update transaction limit if response code 00
                if (response.getBody().getResponseCode().equalsIgnoreCase("00")) {
                    try {
                        JSONObject value = new JSONObject();
                        TrxLimit trxLimit = new TrxLimit();
                        String amount = paymentResp.getAmount() != null ? paymentResp.getAmount() : "0";
                        int trxType = TrxLimit.PAYMENT;

                        double d = Double.parseDouble(amount);
                        long amount_convert = (new Double(d)).longValue();
                        trxLimit.LimitUpdate(request.getMsisdn(), request.getCustomerLimitType(), trxType, amount_convert, value, sqlconf);
                        //LibFunctionUtil.mailNotif(request.getCustomerEmail(),mbApiResp, templateMailNotif, request.getLanguage());
	    			      
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("Update transaction limit failed :" + e.getMessage());
                    }
                }
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                System.out.println("run error");
                mbApiResp = MbJsonUtil.createPdamPaymentErrorResponse(response.getBody(), request.getLanguage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            String errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_PAYMENT_REQUEST_EN : MbConstant.ERROR_PAYMENT_REQUEST_ID;
            if (e.getCause().getClass().getName().equalsIgnoreCase("java.net.SocketTimeoutException")) {
                //time out exception
                errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_TIMEOUT_PAYMENT_REQUEST_EN : MbConstant.ERROR_TIMEOUT_PAYMENT_REQUEST_ID;
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
            log.info("Error : " + e.getCause().getMessage());
        }

        return mbApiResp;
    }
}
