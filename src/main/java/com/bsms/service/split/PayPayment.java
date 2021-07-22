package com.bsms.service.split;

import com.bsms.domain.SpMerchant;
import com.bsms.repository.SpMerchantRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.base.BaseResponse;
import com.bsms.restobjclient.payment.Content;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.LibFunctionUtil;
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
@Service("payPayment")
public class PayPayment extends MbBaseServiceImpl implements MbService {

    @Autowired
    SpMerchantRepository spMerchantRepository;

    @Autowired
    private ApplicationContext context;

    @Context
    private ContainerRequestContext requestContext;

    @Context
    private HttpHeaders header;

    @Value("${ubp.payment}")
    private String ubpPaymentUrl;

    @Value("${switcher.payment}")
    private String svPaymentUrl;

    @Value("${umrah.payment}")
    private String umrahPaymentUrl;

    @Value("${haji.payment}")
    private String hajiPaymentUrl;

    @Value("${bpjstk.payment}")
    private String bpjstkPaymentUrl;

    @Value("${rest.template.timeout}")
    private int restTimeout;
    
    @Value("${template.mail_notif}")
    private String templateMailNotif;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp response;

        log.info("pay payment split run");

        String requestPath = requestContext.getUriInfo().getPath();
        String requestParam = requestPath.substring(requestPath.lastIndexOf('/') + 1);

        String billerId = request.getBillerid() != null ? request.getBillerid() : requestParam;
        log.info("biller id : " + billerId);

//        SpMerchant result = spMerchantRepository.findBySpMerchantId(billerId);
        List<SpMerchant> result = spMerchantRepository.findAllBySpMerchantId(billerId);

        String paymentType = request.getPayment_type() != null ? request.getPayment_type() : null;

        if (paymentType != null) {
            log.info("Pelunasan Haji dan Umrah Payment Type");
            String url = paymentType.equals("90001") ? hajiPaymentUrl : umrahPaymentUrl;
            response = hajiUmrahPayment(request, url);
        } else {
            if (result.size() != 0) {
                if (result.get(0).getServiceprovider() == 0) {
                    request.setBillerid(billerId);
                    response = switcherPayment(request);
                } else {
                    if (billerId.equals("88999")) { //if bpjstk
                        response = bpjstkPayment(request, bpjstkPaymentUrl);
                    } else {
                        response = ubpPayment(request, billerId);
                    }

                }
            } else {
                log.info("empty sp_merchant");
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

    private MbApiResp switcherPayment(MbApiReq request) throws Exception {
        MbApiResp response;
        MbService service = (MbService) context.getBean("doPaymentPurchase");
        response = service.process(header, requestContext, request);
        
        return response;
    }

    private MbApiResp ubpPayment(MbApiReq request, String billerId) {
        MbApiResp mbApiResp;
        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = ubpPaymentUrl;

            log.info("ubp url : " + url);
            log.info("request : " + new Gson().toJson(req));

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            BaseResponse paymentInquiryResp = response.getBody();

            log.info("::: do payment Response :::");
            log.info(new Gson().toJson(response.getBody()));

            log.info("base response : " + new Gson().toJson(paymentInquiryResp));

            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
                LibFunctionUtil.mailNotif(request.getCustomerEmail(),mbApiResp, templateMailNotif, request.getLanguage());
                
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }
        } catch (Exception e) {
            String errorDefault = "Permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = "Request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }
        return mbApiResp;
    }

    private MbApiResp hajiUmrahPayment(MbApiReq request, String inquiryUrl) {
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
            log.info("::: doPayment HajiUmrah Response :::");
            log.info("response result : " + new Gson().toJson(response));
            log.info("base response : " + new Gson().toJson(paymentInquiryResp));
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
                LibFunctionUtil.mailNotif(request.getCustomerEmail(),mbApiResp, templateMailNotif, request.getLanguage());
                
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

    private MbApiResp bpjstkPayment(MbApiReq request, String inquiryUrl) {
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
            log.info("::: doPayment BPJSTK Response :::");
            log.info("response result : " + new Gson().toJson(response));
            log.info("base response : " + new Gson().toJson(paymentInquiryResp));
            if (paymentInquiryResp.getResponseCode().equals("00")) {
                mbApiResp = MbJsonUtil.createResponse(response.getBody());
                LibFunctionUtil.mailNotif2(request.getCustomerEmail(),mbApiResp, templateMailNotif, request.getLanguage());
                
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

}
