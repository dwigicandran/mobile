package com.bsms.service.split;

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
import java.util.List;

//Addition By Dwi S - September 2020

@Slf4j
@Service("paymentInquiry")
public class PaymentInquiry extends MbBaseServiceImpl implements MbService {

    @Autowired
    SpMerchantRepository spMerchantRepository;

    @Autowired
    private ApplicationContext context;

    @Context
    private ContainerRequestContext requestContext;

    @Context
    private HttpHeaders header;

    @Value("${ubp.inquiry}")
    private String ubpUrl;

    @Value("${umrah.inquiry}")
    private String umrahInquiryUrl;

    @Value("${haji.inquiry}")
    private String hajiInquiryUrl;

    @Value("${bpjstk.inquiry}")
    private String bpjstkInquiryUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp response;
        MbApiTxLog txLog = new MbApiTxLog();

        log.info("payment split runnning !");
        log.info("request : " + new Gson().toJson(request));

        String requestPath = requestContext.getUriInfo().getPath();
        String requestParam = requestPath.substring(requestPath.lastIndexOf('/') + 1);


        String billerId = request.getBillerid() != null ? request.getBillerid() : requestParam;
//        SpMerchant result = spMerchantRepository.findBySpMerchantId(billerId);
        List<SpMerchant> result = spMerchantRepository.findAllSpMerchantByMerchantId(billerId);

        String paymentType = request.getPayment_type() != null ? request.getPayment_type() : null;

        if (paymentType != null) {
            log.info("Pelunasan Haji dan Umrah Payment Type");
            String url = paymentType.equals("90001") ? hajiInquiryUrl : umrahInquiryUrl;
            response = hajiUmrahInquiry(request, url);
        } else {
            if (result.size() != 0) {

                log.info("getservice provider : " + result.get(0).getServiceprovider());

                if (result.get(0).getServiceprovider() == 0) {
                    response = switcherInquiry(request);
                } else {

                    if (billerId.equals("88999")) { //jika bpjstk
                        response = bpjstTkInquiry(request, bpjstkInquiryUrl);
                    } else {
                        response = ubpInquiry(request, billerId);
                    }
                }
            } else {
                String msg = "Unknown Service Provider";
                Content content = new Content();
                content.setKey("ErrorCode");
                content.setValue(msg);
                response = MbJsonUtil.createSPMerchantResponse(msg, content);
                log.info(msg);
            }
        }
        return response;
    }

    private MbApiResp ubpInquiry(MbApiReq request, String billerId) {
        MbApiResp mbApiResp;

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = ubpUrl;

            log.info("ubp url : " + url);
            log.info("request : " + new Gson().toJson(req));

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            log.info("response : " + response);

            BaseResponse paymentInquiryResp = response.getBody();
            log.info("::: doInquiry payment Response :::");
            log.info("response result : " + new Gson().toJson(response));
            log.info("base response : " + new Gson().toJson(paymentInquiryResp));
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

        } catch (Exception e) {
            log.info("exception : " + e.getCause().getMessage());
            String errorDefault = e.getCause().getMessage() + ", permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + ", request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }

        return mbApiResp;
    }

    private MbApiResp hajiUmrahInquiry(MbApiReq request, String inquiryUrl) {
        MbApiResp mbApiResp;

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = inquiryUrl;

            log.info("Haji Umrah Url : " + url);
            log.info("request : " + new Gson().toJson(req));

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            log.info("response : " + response);

            BaseResponse paymentInquiryResp = response.getBody();
            log.info("::: doInquiry HajiUmrah Response :::");
            log.info("response result : " + new Gson().toJson(response));
            log.info("base response : " + new Gson().toJson(paymentInquiryResp));
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

        } catch (Exception e) {
            log.info("exception : " + e.getCause().getMessage());
            String errorDefault = e.getCause().getMessage() + ", permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + ", request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }

        return mbApiResp;
    }

    private MbApiResp bpjstkInquiry(MbApiReq request, String inquiryUrl) {
        MbApiResp mbApiResp;

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = inquiryUrl;

            log.info("BPJSTK Url : " + url);
            log.info("request : " + new Gson().toJson(req));

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            log.info("response : " + response);

            BaseResponse paymentInquiryResp = response.getBody();
            log.info("::: doInquiry BPJSTK Response :::");
            log.info("response result : " + new Gson().toJson(response));
            log.info("base response : " + new Gson().toJson(paymentInquiryResp));
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

        } catch (Exception e) {
            log.info("exception : " + e.getCause().getMessage());
            String errorDefault = e.getCause().getMessage() + ", permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + ", request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }

        return mbApiResp;
    }


    private MbApiResp bpjstTkInquiry(MbApiReq request, String inquiryUrl) {
        MbApiResp mbApiResp;

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = inquiryUrl;

            log.info("BPJSTK Url : " + url);
            log.info("request : " + new Gson().toJson(req));

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            log.info("response : " + response);

            BaseResponse paymentInquiryResp = response.getBody();
            log.info("::: doInquiry BPJSTK Response :::");
            log.info("response result : " + new Gson().toJson(response));
            log.info("base response : " + new Gson().toJson(paymentInquiryResp));
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }

        } catch (Exception e) {
            log.info("exception : " + e.getCause().getMessage());
            String errorDefault = e.getCause().getMessage() + ", permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + ", request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }

        return mbApiResp;
    }


    private MbApiResp switcherInquiry(MbApiReq request) throws Exception {
        MbApiResp response;
        MbService service = (MbService) context.getBean("doPaymentInquiry");
        response = service.process(header, requestContext, request);
        return response;
    }


}
