package com.bsms.service.ziswaf;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.ziswaf.getListProjectWakafResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.service.transfer.MbInquiryOnlineTrfService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("getListProjectWakaf")
public class getListProjectWakaf extends MbBaseServiceImpl implements MbService {

    @Value("${ziswaf.getListProjectWakaf}")
    private String getListProjectWakaf;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;

    RestTemplate restTemplate = new RestTemplate();

    MbApiResp mbApiResp;

    Client client = ClientBuilder.newClient();

    //add by Dwi S
    @Value("${rest.template.timeout}")
    private int restTimeout;

    private static Logger log = LoggerFactory.getLogger(MbInquiryOnlineTrfService.class);

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {

        MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);

        System.out.println("::: getListProjectWakaf Ziswaf Microservices Request :::");
        System.out.println(new Gson().toJson(request));

        try {

            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            //add By Dwi S
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = getListProjectWakaf;

            ResponseEntity<getListProjectWakafResp> response = restTemps.exchange(url, HttpMethod.POST, req, getListProjectWakafResp.class);
            getListProjectWakafResp getListProjectWakafResp = response.getBody();

            System.out.println("::: getListProjectWakaf Ziswaf Microservices Response :::");
            System.out.println(new Gson().toJson(response.getBody()));

            mbApiResp = MbJsonUtil.createResponseBank(getListProjectWakafResp.getResponseCode(), getListProjectWakafResp.getResponseMessage(), getListProjectWakafResp.getResponseContent());


        } catch (Exception e) {
//            mbApiResp = MbJsonUtil.createResponseTrf("99",
//                    e.toString(),
//                    null, "");
//            MbLogUtil.writeLogError(log, "99", e.toString());
            log.info("exception : " + e.getCause().getMessage());
            String errorDefault = "permintaan tidak dapat diproses, silahkan dicoba beberapa saat lagi.";
            if (request.getLanguage().equals("en")) {
                errorDefault = "request can't be process, please try again later.";
            }
            mbApiResp = MbJsonUtil.createResponseBank("99", errorDefault, null);
        }

        txLog.setResponse(mbApiResp);
        txLogRepository.save(txLog);


        return mbApiResp;
    }

}
