package com.bsms.service.ziswaf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.json.JSONObject;
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

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.emoney.doInquiryEmoneyResp;
import com.bsms.restobjclient.limit.InfoLimitDispResp;
import com.bsms.restobjclient.qris.doInquiryQRISResp;
import com.bsms.restobjclient.transfer.Bank;
import com.bsms.restobjclient.transfer.BankDispResp;
import com.bsms.restobjclient.transfer.ContentInqTrf;
import com.bsms.restobjclient.transfer.InquiryTrfDispResp;
import com.bsms.restobjclient.transfer.InquiryTrfReq;
import com.bsms.restobjclient.transfer.InquiryTrfResp;
import com.bsms.restobjclient.ziswaf.getListAmilResp;
import com.bsms.restobjclient.ziswaf.getListWakafResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.service.transfer.MbInquiryOnlineTrfService;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.dto.accountlist.ListOfAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("getListWakaf")
public class getListWakaf extends MbBaseServiceImpl implements MbService {

    @Value("${ziswaf.getListWakaf}")
    private String getListWakaf;

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

        System.out.println("::: getListWakaf Microservices Request :::");
        System.out.println(new Gson().toJson(request));

        try {

            HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            //add By Dwi S
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setConnectTimeout(restTimeout);
            ((SimpleClientHttpRequestFactory) restTemps.getRequestFactory()).setReadTimeout(restTimeout);
            String url = getListWakaf;

            ResponseEntity<getListWakafResp> response = restTemps.exchange(url, HttpMethod.POST, req, getListWakafResp.class);
            getListWakafResp getListWakafResp = response.getBody();

            System.out.println("::: getListWakaf Microservices Response :::");
            System.out.println(new Gson().toJson(response.getBody()));

            mbApiResp = MbJsonUtil.createResponseBank(getListWakafResp.getResponseCode(), getListWakafResp.getResponseMessage(), getListWakafResp.getResponseContent());


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
