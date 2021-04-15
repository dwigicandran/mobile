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
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

@Slf4j
@Service("prepaidInquiry")
public class PrepaidInquriy extends MbBaseServiceImpl implements MbService {

    @Autowired
    SpMerchantRepository spMerchantRepository;

    @Autowired
    private MbTxLogRepository txLogRepository;

    @Autowired
    private ApplicationContext context;

    @Context
    private ContainerRequestContext requestContext;

    @Context
    private HttpHeaders header;

    @Value("${ubp.inquiry}")
    private String ubpInquiryUrl;

    @Value("${switcher.prepaid.inquiry}")
    private String switcherInquiryUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp response = null;
        MbApiTxLog txLog = new MbApiTxLog();

        log.info("Prepaid Inquiry Split Runnning : ");
        log.info("Prepaid Inquiry Split Request : " + new Gson().toJson(request));

        String requestPath = requestContext.getUriInfo().getPath();
        String requestParam = requestPath.substring(requestPath.lastIndexOf('/') + 1);
//        String billerId = request.getBillerid() != null ? request.getBillerid() : requestParam;
        String billerId = requestParam != null || !requestParam.equals("") ? requestParam : request.getBillerid() != null ? request.getBillerid() : "";

        SpMerchant result = spMerchantRepository.findBySpMerchantId(billerId);

        log.info("REQUEST PARAM : " + requestParam);
        log.info("BILLER ID : " + billerId);

        if (result != null) {
            request.setBillerid(billerId);
            if (result.getServiceprovider() == 0) {
                response = switcherInquiry(request, billerId);
                log.info("Prepaid To Switcher Services");
            } else {
                log.info("Prepaid TO UBP Services");
                response = ubpInquiry(request, billerId);
            }
        } else {
            String msg = "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            Content content = new Content();
            content.setKey("ErrorCode");
            content.setValue(msg);
            response = MbJsonUtil.createSPMerchantResponse(msg, content);

            MbLogUtil.writeLogError(log, "Biller Unknown", MbApiConstant.NOT_AVAILABLE);
        }

        log.info("MOBILE API RESPONSE : " + new Gson().toJson(response));
        txLog.setId(response.getTransactionId());
        txLog.setResponse(response);
        txLog.setRequest(request);
        txLogRepository.save(txLog);

        log.info("Transaction Log : " + new Gson().toJson(txLog));

        return response;
    }

    private MbApiResp ubpInquiry(MbApiReq request, String billerId) {
        log.info("UBP Biller : " + billerId);

        MbApiResp mbApiResp = new MbApiResp();
        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = ubpInquiryUrl + "/" + billerId;

            log.info("Split UBP url : " + url);
            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);

            log.info("UBP Response : " + new Gson().toJson(response));

            BaseResponse paymentInquiryResp = response.getBody();
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

        } catch (Exception e) {
            String errorDefault = "permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = "request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
            e.printStackTrace();
        }
        return mbApiResp;
    }

    private MbApiResp switcherInquiry(MbApiReq request, String billerId) throws Exception {
        log.info("Switcher Biller : " + billerId);

        MbApiResp mbApiResp = new MbApiResp();

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = switcherInquiryUrl + "/" + billerId;
            log.info("Split Switcher url : " + url);

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            log.info("Switcher Response : " + new Gson().toJson(response));

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
