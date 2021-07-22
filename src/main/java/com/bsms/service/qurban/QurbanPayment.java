package com.bsms.service.qurban;


import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
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

//By Dwi S - Oktober 2020

@Slf4j
@Service("qurbanPayment")
public class QurbanPayment extends MbBaseServiceImpl implements MbService {

    @Autowired
    private MbTxLogRepository txLogRepository;

    MbApiResp mbApiResp;

    @Value("${qurban.payment}")
    private String doPaymentUrl;

    @Value("${sql.conf}")
    private String sqlconf;

    @Value("${rest.template.timeout}")
    private int restTimeout;
    
    @Value("${template.mail_notif}")
    private String templateMailNotif;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {

        MbApiTxLog txLog = new MbApiTxLog();

        log.info("::: Payment Qurban Request RUN :::");
        log.info(new Gson().toJson(request));

        String errorDefault = "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
        if (request.getLanguage().equals("en")) {
            errorDefault = "Request can't be process, please try again later.";
        }

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            String url = doPaymentUrl;
            RestTemplate restTemplate = new RestTemplate();
            //set resttemplate timeout
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(restTimeout);

            ResponseEntity<BaseResponse> response = restTemplate.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            log.info("QURBAN INQUIRY PAYMENT : " + url);
            log.info("::: Qurban Payment Response :::");
            log.info(new Gson().toJson(response.getBody()));

            if (response.getBody() != null) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                MbLogUtil.writeLogError(log, "Response body null", MbApiConstant.NOT_AVAILABLE);
                mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
            }

            //update transaction limit if response code 00
            if (response.getBody().getResponseCode().equalsIgnoreCase("00")) {
                try {
                    JSONObject value = new JSONObject();
                    TrxLimit trxLimit = new TrxLimit();
                    int trxType = TrxLimit.PURCHASE;

                    double d = Double.parseDouble(request.getDenomId());
                    long amount_convert = (new Double(d)).longValue();
                    trxLimit.LimitUpdate(request.getMsisdn(), request.getCustomerLimitType(), trxType, amount_convert, value, sqlconf);
                    LibFunctionUtil.mailNotif(request.getCustomerEmail(),mbApiResp, templateMailNotif, request.getLanguage());
  	       		     
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("Update transaction limit failed :" + e.getMessage());
                }
            }

        } catch (Exception e) {
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
            MbLogUtil.writeLogError(log, "99", e.toString());
        }
        txLog.setId(mbApiResp.getTransactionId());
        txLog.setResponse(mbApiResp);
        txLog.setRequest(request);
        txLogRepository.save(txLog);


        return mbApiResp;
    }
}
