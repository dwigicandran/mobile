package com.bsms.service.qurban;

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

//By Dwi S - Oktober 2020
@Slf4j
@Service("qurbanInquiry")
public class QurbanInquiry extends MbBaseServiceImpl implements MbService {

    @Autowired
    private MbTxLogRepository txLogRepository;

    MbApiResp mbApiResp;

    @Value("${qurban.inquiry}")
    private String doInquiryUrl;

    @Value("${sql.conf}")
    private String sqlconf;

    @Value("${rest.template.timeout}")
    private int restTimeout;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        MbApiTxLog txLog = new MbApiTxLog();

        log.info("::: doInquiry Qurban Request RUN :::");
        log.info(new Gson().toJson(request));

        JSONObject value = new JSONObject();
        TrxLimit trxLimit = new TrxLimit();
        int trxType = TrxLimit.PURCHASE;

        String limitResponseCode = null;
        String response_msg = null;
        String errorDefault = MbConstant.ERROR_REQUEST_ID;
        if (request.getLanguage().equals("en")) {
            errorDefault = MbConstant.ERROR_REQUEST_EN;
        }

        //get qurban amount
        try {
            double qurbanAmount = Double.parseDouble(request.getDenomId()); //transaction amount
            long amount_convert = (new Double(qurbanAmount)).longValue(); //129
            limitResponseCode = trxLimit.checkLimit(request.getMsisdn(), request.getCustomerLimitType(), trxType, amount_convert, value, sqlconf);
        } catch (Exception e) {
            log.info("Limit Check Error : " + e.getMessage());
        }

        if ("00".equals(limitResponseCode)) {
            try {
                HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
                String url = doInquiryUrl;
                RestTemplate restTemplate = new RestTemplate();
                //set rest template timeout
                ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(restTimeout);
                ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(restTimeout);

                ResponseEntity<BaseResponse> response = restTemplate.exchange(url, HttpMethod.POST, req, BaseResponse.class);

                log.info("QURBAN INQUIRY URL : " + url);
                log.info("::: Qurban Inquiry Response :::");
                log.info(new Gson().toJson(response.getBody()));

                if (response.getBody() != null) {
                    mbApiResp = MbJsonUtil.createResponse(response.getBody());
                } else {
                    MbLogUtil.writeLogError(log, "Response body is empty", MbApiConstant.NOT_AVAILABLE);
                    mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
                }

            } catch (Exception e) {
                mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
                MbLogUtil.writeLogError(log, "99", e.getMessage());
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

        //save to log
        txLog.setId(mbApiResp.getTransactionId());
        txLog.setResponse(mbApiResp);
        txLog.setRequest(request);
        txLogRepository.save(txLog);

        return mbApiResp;
    }


}
