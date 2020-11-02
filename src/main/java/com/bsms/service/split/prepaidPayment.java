package com.bsms.service.split;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.SpMerchant;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.SpMerchantRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.restobjclient.payment.Content;
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
import java.util.Optional;

@Slf4j
@Service("prepaidPayment")
public class prepaidPayment extends MbBaseServiceImpl implements MbService {

    @Autowired
    SpMerchantRepository spMerchantRepository;

    @Autowired
    private MbTxLogRepository txLogRepository;

    @Value("${ubp.payment}")
    private String ubpInquiryUrl;

    @Value("${switcher.prepaid.payment}")
    private String switcherInquiryUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp response;
        MbApiTxLog txLog = new MbApiTxLog();
        String billerId = "";

        log.info("Prepaid Payment Split Runnning : ");
        log.info("Prepaid Payment Split Request : " + new Gson().toJson(request));

        String errorDefault = "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
        if (request.getLanguage().equals("en")) {
            errorDefault = "Request can't be process, please try again later.";
        }

        Optional<MbApiTxLog> transactionLog = txLogRepository.findById(request.getTransaction_id());
        MbApiReq inquiryRequest = transactionLog.get().getRequest();

        if (inquiryRequest != null) {
            log.info("transaction ada");
            log.info("Biller Code : " + inquiryRequest.getTransactionId());

            billerId = inquiryRequest.getBillerid() != null ? inquiryRequest.getBillerid() : request.getBillerid();
            SpMerchant result = spMerchantRepository.findBySpMerchantId(billerId);

            if (result.getServiceprovider() == 0) {
                response = svPayment(request, billerId);
                log.info("Prepaid To Switcher Services");
            } else {
                log.info("Prepaid TO UBP Services");
                response = ubpPayment(request, billerId);
            }

        } else {
            Content content = new Content();
            content.setKey("ErrorCode");
            content.setValue(errorDefault);
            response = MbJsonUtil.createSPMerchantResponse(errorDefault, content);
            MbLogUtil.writeLogError(log, "Unkown Biller", MbApiConstant.NOT_AVAILABLE);
        }

        txLog.setId(response.getTransactionId());
        txLog.setResponse(response);
        txLog.setRequest(request);
        txLogRepository.save(txLog);

        return response;
    }

    private MbApiResp ubpPayment(MbApiReq request, String billerId) {
        log.info("UBP Biller : " + billerId);

        MbApiResp mbApiResp;
        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = ubpInquiryUrl;

            log.info("Split UBP url : " + url);
            log.info("UBP Request : " + req);
            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            log.info("UBP Response : " + new Gson().toJson(response));

            BaseResponse paymentInquiryResp = response.getBody();
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

        } catch (Exception e) {
            String errorDefault = e.getCause().getMessage() + ", permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + ", request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }
        return mbApiResp;
    }

    private MbApiResp svPayment(MbApiReq request, String billerId) {
        MbApiResp mbApiResp;
        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = switcherInquiryUrl;
            log.info("Split Switcher url : " + url);
            log.info("Switcher request : " + new Gson().toJson(request));

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            log.info("Switcher Responnse : " + new Gson().toJson(response));

            BaseResponse paymentInquiryResp = response.getBody();
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

        } catch (Exception e) {
            String errorDefault = e.getCause().getMessage() + ", permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + ", request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }

        return mbApiResp;
    }


}
