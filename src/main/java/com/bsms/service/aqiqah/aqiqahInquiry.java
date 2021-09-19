package com.bsms.service.aqiqah;

import com.bsms.cons.MbApiConstant;
import com.bsms.cons.MbConstant;
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

@Slf4j
@Service("aqiqahInquiry")
public class aqiqahInquiry extends MbBaseServiceImpl implements MbService {

    @Autowired
    private MbTxLogRepository txLogRepository;

    MbApiResp mbApiResp;

    @Value("${sql.conf}")
    private String sqlconf;

    @Value("${aqiqah.inquiry}")
    private String doInquiryUrl;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiTxLog txLog = new MbApiTxLog();

        log.info(new Gson().toJson(request));

        String limitResponseCode = null;
        String response_msg = null;
        String errorDefault = MbConstant.ERROR_REQUEST_ID;
        if (request.getLanguage().equals("en")) {
            errorDefault = MbConstant.ERROR_REQUEST_EN;
        }

        //get aqiqah amount
        try {
            double aqiqahAmount = Double.parseDouble(request.getDenomId()); //transaction amount
            long amount_convert = (new Double(aqiqahAmount)).longValue(); //129
//            limitResponseCode = trxLimit.checkLimit(request.getMsisdn(), request.getCustomerLimitType(), trxType, amount_convert, value, sqlconf);
            log.info("Aqiqah amount limit check response : " + limitResponseCode);
        } catch (Exception e) {
            log.info("Limit Check Error : " + e.getMessage());
        }
        
        limitResponseCode = checklimitTransaction(request.getDenomId(), request.getCustomerLimitType(), 
        		request.getMsisdn(), TrxLimit.PURCHASE, request.getLanguage());

        if ("00".equals(limitResponseCode)) {
            try {
                HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
                String url = doInquiryUrl;
                RestTemplate restTemplate = new RestTemplate();
                ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(30000);
                ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(30000);

                ResponseEntity<BaseResponse> response = restTemplate.exchange(url, HttpMethod.POST, req, BaseResponse.class);

                log.info("::: Aqiqah Inquiry URL :" + url);
                log.info("::: Aqiqah Inquiry Response :::");
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
        } else if ("01".equals(limitResponseCode)) {
            response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_FINANCIAL_EN : MbConstant.ERROR_LIMIT_FINANCIAL_ID;
            mbApiResp = MbJsonUtil.createResponseTrf("01", response_msg, null, "");
        } else if ("02".equals(limitResponseCode)) {
            response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_EXCEED_EN : MbConstant.ERROR_LIMIT_EXCEED_ID;
            mbApiResp = MbJsonUtil.createResponseTrf("02", response_msg, null, "");
        } else {
            response_msg = request.getLanguage().equalsIgnoreCase("en") ? MbConstant.ERROR_LIMIT_CHECK_EN : MbConstant.ERROR_LIMIT_CHECK_ID;
            mbApiResp = MbJsonUtil.createResponseTrf("03", response_msg, null, "");
        }


        txLog.setId(mbApiResp.getTransactionId());
        txLog.setResponse(mbApiResp);
        txLog.setRequest(request);
        txLogRepository.save(txLog);


        return mbApiResp;
    }
}
