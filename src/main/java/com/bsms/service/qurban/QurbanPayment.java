package com.bsms.service.qurban;


import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service("qurbanPayment")
public class QurbanPayment  extends MbBaseServiceImpl implements MbService {

    @Autowired
    private MbTxLogRepository txLogRepository;

    MbApiResp mbApiResp;

    @Value("${qurban.payment}")
    private String doPaymentUrl;

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
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(30000);
            ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(30000);

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
