package com.bsms.service.cardotp.odc;

import com.bsms.cons.MbApiConstant;
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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.math.BigDecimal;

@Service("doOdcPayment")
public class OdcPayment extends MbBaseServiceImpl implements MbService {

    private static Logger log = LoggerFactory.getLogger(OdcPayment.class);

    @Value("${cardotp.odc.payment}")
    private String odcPaymentUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Value("${sql.conf}")
    private String sqlconf;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp mbApiResp;

        log.info("Get CardOtp Odc Payment Request : " + new Gson().toJson(request));
        String errorDefault = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_REQUEST_EN : MbConstant.ERROR_REQUEST_ID;

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = odcPaymentUrl;

            ResponseEntity<BaseResponse> restResponse = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            log.info("Get CardOtp Odc Payment Response : " + new Gson().toJson(restResponse));

            BaseResponse paymentResponse = restResponse.getBody();
            if (paymentResponse.getResponseCode().equals("00")) {

                try {
                    JSONObject value = new JSONObject();
                    TrxLimit trxLimit = new TrxLimit();

                    String amount = request.getAmount() != null ? request.getAmount() : "0";
                    System.out.println("amount" + amount);
                    Integer trxType = MbApiConstant.PURCHASE;

                    double d = Double.parseDouble(amount);
                    long amount_convert = (new Double(d)).longValue();

                    BigDecimal amt = new BigDecimal(amount);
//                    mbTrxLimit.LimitUpdate(request.getMsisdn(), request.getCustomerLimitType(), trxType, amt);

                    trxLimit.LimitUpdate(request.getMsisdn(), request.getCustomerLimitType(), trxType, amount_convert, value, sqlconf);


                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("Update transaction limit failed :" + e.getMessage());
                }

                mbApiResp = MbJsonUtil.createResponse(restResponse.getBody());
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
