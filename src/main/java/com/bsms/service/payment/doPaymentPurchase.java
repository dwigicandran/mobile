package com.bsms.service.payment;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.service.transfer.MbInquiryOnlineTrfService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

//Addition by Dwi S - Agustus 2020

@Service("doPaymentPurchase")
public class doPaymentPurchase extends MbBaseServiceImpl implements MbService {

    @Value("${payment.doPayment}")
    private String doPayment;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Autowired
    private MbTxLogRepository txLogRepository;

    private MbApiResp mbApiResp;

    private static Logger log = LoggerFactory.getLogger(MbInquiryOnlineTrfService.class);

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiTxLog txLog = new MbApiTxLog();
        String errorDefault = "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
        if (request.getLanguage().equals("en")) {
            errorDefault = "Request can't be process, please try again later.";
        }
        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            String url = doPayment;

            System.out.println("dopayment request : " + new Gson().toJson(req.getBody()));

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            System.out.println("-----------------------------------------------------------");
            System.out.println("Response From Switcher : \n" + new Gson().toJson(response));
            System.out.println("---------------------end Response -------------------------");

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

        txLog.setResponse(mbApiResp);
        txLog.setRequest(request);
        txLogRepository.save(txLog);

        return mbApiResp;
    }

}