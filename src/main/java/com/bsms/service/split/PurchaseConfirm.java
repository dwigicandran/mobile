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
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("purchaseConfirm")
public class PurchaseConfirm extends MbBaseServiceImpl implements MbService {
    @Autowired
    SpMerchantRepository spMerchantRepository;

    @Autowired
    private MbTxLogRepository txLogRepository;

    @Value("${ubp.confirm}")
    private String ubpInquiryUrl;

    @Value("${switcher.prepaid.confirm}")
    private String switcherInquiryUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    private String errorDefaultId = ", permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
    private String errorDefaultEn = ", request can't be process, please try again later.";

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp response;
        String billerId = "";

        log.info("Purchase Confirm Split Runnning ");
        log.info("Purchase Confirm Split Request : " + new Gson().toJson(request));

        String errorDefault = "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
        if (request.getLanguage().equals("en")) {
            errorDefault = "Request can't be process, please try again later.";
        }

        try {
            Optional<MbApiTxLog> transactionLog = txLogRepository.findById(request.getTransaction_id());
            log.info("transaction log : " + new Gson().toJson(transactionLog));
            MbApiReq inquiryRequest = transactionLog.get().getRequest();

            if (inquiryRequest != null) {
                log.info("Biller Code : " + inquiryRequest.getTransactionId());
                billerId = inquiryRequest.getBillerid() != null ? inquiryRequest.getBillerid() : request.getBillerid();
//                SpMerchant result = spMerchantRepository.findBySpMerchantId(billerId);
                List<SpMerchant> result = spMerchantRepository.findAllSpMerchantByMerchantId(billerId);
                log.info("SPMERCHANT Result : " + new Gson().toJson(result));

                if (result.size() != 0) {
                    if (result.get(0).getServiceprovider() == 0) {
                        response = svConfirm(request, billerId);
                        log.info("Purchase To Switcher Services");
                    } else {
                        log.info("Purchase To UBP Services");
                        response = ubpConfirm(request, billerId);
                    }
                } else {
//                    log.info("Purchase To UBP Services");
//                    response = ubpConfirm(request, billerId);
                    String msg = "Unknown Service Provider";
                    Content content = new Content();
                    content.setKey("ErrorCode");
                    content.setValue(msg);
                    response = MbJsonUtil.createSPMerchantResponse(msg, content);
                    log.info(msg);
                }
            } else {
                Content content = new Content();
                content.setKey("ErrorCode");
                content.setValue("Unknown Service Provider");
                response = MbJsonUtil.createSPMerchantResponse(errorDefault, content);
                log.info("Unknown Service Provider");
            }
            log.info("biller id : " + billerId);
        } catch (Exception e) {
            Content content = new Content();
            content.setKey("ErrorCode");
            content.setValue(errorDefault);
            response = MbJsonUtil.createSPMerchantResponse(errorDefault, content);
            MbLogUtil.writeLogError(log, "Biller Unknown", MbApiConstant.NOT_AVAILABLE);
        }
        return response;
    }

    private MbApiResp ubpConfirm(MbApiReq request, String billerId) {
        log.info("UBP Biller : " + billerId);
        MbApiResp mbApiResp;
        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);

            String url = ubpInquiryUrl;
            log.info("Split UBP url : " + url);
            log.info("UBP Request : " + new Gson().toJson(request));

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            log.info("UBP Response : " + new Gson().toJson(response));

            BaseResponse paymentInquiryResp = response.getBody();
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

        } catch (Exception e) {
            String errorDefault = e.getCause().getMessage() + errorDefaultId;
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + errorDefaultEn;
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }
        return mbApiResp;
    }

    private MbApiResp svConfirm(MbApiReq request, String billerId) {
        log.info("Switcher Biller : " + billerId);
        MbApiResp mbApiResp;
        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);

            String url = switcherInquiryUrl;
            log.info("Split Switcher url : " + url);
            log.info("Switcher Request : " + new Gson().toJson(request));

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            log.info("Switcher Response : " + new Gson().toJson(response));

            BaseResponse paymentInquiryResp = response.getBody();
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }
        } catch (Exception e) {
            String errorDefault = e.getCause().getMessage() + errorDefaultId;
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + errorDefaultEn;
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }
        return mbApiResp;
    }
}
