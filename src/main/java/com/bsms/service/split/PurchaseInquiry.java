package com.bsms.service.split;

import com.bsms.cons.MbConstant;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;

@Slf4j
@Service("purchaseInquiry")
public class PurchaseInquiry extends MbBaseServiceImpl implements MbService {
    @Autowired
    SpMerchantRepository spMerchantRepository;

    @Autowired
    private MbTxLogRepository txLogRepository;

    @Context
    private ContainerRequestContext requestContext;

    @Context
    private HttpHeaders header;

    @Value("${ubp.inquiry}")
    private String ubpInquiryUrl;

    @Value("${switcher.prepaid.inquiry}")
    private String switcherPrepaidInquiryUrl;

    @Value("${switcher.inquiry}")
    private String switcherInquiryUrl;

    @Value("${sql.conf}")
    private String sqlconf;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    private String errorDefaultId = ", permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
    private String errorDefaultEn = ", request can't be process, please try again later.";

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiResp response;
        MbApiTxLog txLog = new MbApiTxLog();

        log.info("Purchase Inquiry Split Runnning : ");
        log.info("Purchase Inquiry Split Request : " + new Gson().toJson(request));

        String requestPath = requestContext.getUriInfo().getPath();
        String requestParam = requestPath.substring(requestPath.lastIndexOf('/') + 1);
        String billerId = requestParam != null && !requestParam.equals("purchaseInquiry") ? requestParam : request.getBillerid();
        log.info("BILLER ID : " + billerId);
//        log.info("Biller Id : " + request.getBillerid());
//        log.info("Param : " + requestParam);

//        SpMerchant result = spMerchantRepository.findBySpMerchantId(billerId);
        List<SpMerchant> result = spMerchantRepository.findAllBySpMerchantId(billerId);

        log.info("SPMerchant result : " + new Gson().toJson(result));
//        log.info("service provider : " + result.get(0).getServiceprovider());

//        log.info("REQUEST PARAM : " + requestParam);
        log.info("result size : " + result.size());


        if (result.size() != 0) {
            request.setBillerid(billerId);
            if (result.get(0).getServiceprovider() == 0) {
                response = switcherInquiry(request, billerId);
                log.info("Purchase To Switcher Services");
            } else {
                log.info("Purchase TO UBP Services");
                response = ubpInquiry(request, billerId);
            }
        } else {
            String msg = "Unknown Service Provider";
            Content content = new Content();
            content.setKey("ErrorCode");
            content.setValue(msg);
            response = MbJsonUtil.createSPMerchantResponse(msg, content);
            log.info(msg);
        }

        log.info("MOBILE API RESPONSE : " + new Gson().toJson(response));
        txLog.setId(response.getTransactionId());
        txLog.setResponse(response);
        txLog.setRequest(request);
        txLogRepository.save(txLog);

        return response;
    }

    private MbApiResp ubpInquiry(MbApiReq request, String billerId) {
        log.info("UBP Biller : " + billerId);
        MbApiResp mbApiResp;
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
                int trxType = request.getModul_id().equalsIgnoreCase("PU") ? TrxLimit.PURCHASE : TrxLimit.PAYMENT;
                String limitResponse = checkLimit(response.getBody().getAmount(), request.getCustomerLimitType(), request.getMsisdn(), trxType);
                String response_msg = "";

                if (limitResponse.equalsIgnoreCase("01")) {
                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_FINANCIAL_EN : MbConstant.ERROR_LIMIT_FINANCIAL_ID;
                    mbApiResp = MbJsonUtil.createResponseTrf("01", response_msg, null, "");
                } else if (limitResponse.equalsIgnoreCase("02")) {
                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_EXCEED_EN : MbConstant.ERROR_LIMIT_EXCEED_ID;
                    mbApiResp = MbJsonUtil.createResponseTrf("02", response_msg, null, "");
                } else {
                    mbApiResp = MbJsonUtil.createResponse(response.getBody());
                }
//                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }
        } catch (Exception e) {
            log.error("error", e);
            String errorDefault = e.getCause().getMessage() + errorDefaultId;
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + errorDefaultEn;
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }
        return mbApiResp;
    }

    private MbApiResp switcherInquiry(MbApiReq request, String billerId) throws Exception {
        log.info("Switcher Biller : " + billerId);
        MbApiResp mbApiResp;

        try {
            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
//            String url = switcherPrepaidInquiryUrl + "/" + billerId;
            String url;

            if (
                //if indiehome
                    billerId.equalsIgnoreCase("0902") || billerId.equalsIgnoreCase("6050")
                            //if doku
                            || billerId.equalsIgnoreCase("6059")
                            //if ziswaf sharing
                            || billerId.equalsIgnoreCase("6060")
                            //if dompet dhuafa
                            || billerId.equalsIgnoreCase("6061")
                            //if kita bisa
                            || billerId.equalsIgnoreCase("6066")
                            //if bhinneka
                            || billerId.equalsIgnoreCase("6027")
                            //if Bumdes
                            || billerId.equalsIgnoreCase("6070")
                            //if ASDP Ferizy
                            || billerId.equalsIgnoreCase("6136")
            ) {
                url = switcherInquiryUrl;
            } else {
                url = switcherPrepaidInquiryUrl + "/" + billerId;
            }

            log.info("Split Switcher url : " + url);
            log.info("Split Switcher request : " + new Gson().toJson(request));

            ResponseEntity<BaseResponse> response = restTemps.exchange(url, HttpMethod.POST, req, BaseResponse.class);
            BaseResponse paymentInquiryResp = response.getBody();

            log.info("Switcher Response : " + new Gson().toJson(response));

            if (paymentInquiryResp.getResponseCode().equals("00")) {

                int trxType = request.getModul_id().equalsIgnoreCase("PU") ? TrxLimit.PURCHASE : TrxLimit.PAYMENT;
                String limitResponse = checkLimit(response.getBody().getAmount(), request.getCustomerLimitType(), request.getMsisdn(), trxType);
                String response_msg = "";

                if (limitResponse.equalsIgnoreCase("01")) {
                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_FINANCIAL_EN : MbConstant.ERROR_LIMIT_FINANCIAL_ID;
                    mbApiResp = MbJsonUtil.createResponseTrf("01", response_msg, null, "");
                } else if (limitResponse.equalsIgnoreCase("02")) {
                    response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_EXCEED_EN : MbConstant.ERROR_LIMIT_EXCEED_ID;
                    mbApiResp = MbJsonUtil.createResponseTrf("02", response_msg, null, "");
                } else {
                    mbApiResp = MbJsonUtil.createResponse(response.getBody());
                }

//                mbApiResp = MbJsonUtil.createResponse(response.getBody());
            } else {
                mbApiResp = MbJsonUtil.createErrResponse(response.getBody());
            }
        } catch (Exception e) {
            log.error("error", e);
            String errorDefault = e.getCause().getMessage() + errorDefaultId;
            if (request.getLanguage().equals("en")) {
                errorDefault = e.getCause().getMessage() + errorDefaultEn;
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }

        return mbApiResp;
    }


    private String checkLimit(String amount, int customerLimitType, String msisdn, int trxType) {
//        String limitResponseCode = MbConstant.ERROR_NUM_UNKNOWN;
        String limitResponseCode = "00";
        TrxLimit trxLimit = new TrxLimit();
        JSONObject value = new JSONObject();

        try {
            double pdamAmount = Double.parseDouble(amount); //transaction amount
            long amount_convert = (new Double(pdamAmount)).longValue(); //129
            limitResponseCode = trxLimit.checkLimit(msisdn, customerLimitType, trxType, amount_convert, value, sqlconf);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Limit Check Error : " + e.getMessage());
        }

        return limitResponseCode;
    }


}
