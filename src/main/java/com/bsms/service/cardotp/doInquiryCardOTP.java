package com.bsms.service.cardotp;

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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.cardotp.doInquiryCardOTPResp;
import com.bsms.restobjclient.emoney.doInquiryEmoneyResp;
import com.bsms.restobjclient.limit.InfoLimitDispResp;
import com.bsms.restobjclient.transfer.Bank;
import com.bsms.restobjclient.transfer.BankDispResp;
import com.bsms.restobjclient.transfer.ContentInqTrf;
import com.bsms.restobjclient.transfer.InquiryTrfDispResp;
import com.bsms.restobjclient.transfer.InquiryTrfReq;
import com.bsms.restobjclient.transfer.InquiryTrfResp;
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

@Service("doInquiryCardOTP")
public class doInquiryCardOTP extends MbBaseServiceImpl implements MbService {
    @Value("${sql.conf}")
    private String sqlconf;

    @Value("${cardotp.doInquiry}")
    private String doInquiry;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;

    RestTemplate restTemplate = new RestTemplate();

    MbApiResp mbApiResp;

    ResponseEntity<InquiryTrfResp> response;

    Client client = ClientBuilder.newClient();

    private static Logger log = LoggerFactory.getLogger(MbInquiryOnlineTrfService.class);

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {

        MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);

        //=============== cek limit ================//
        String response_msg = null;

        JSONObject value = new JSONObject();
        TrxLimit trxLimit = new TrxLimit();
        int trxType = TrxLimit.CW;

        String response_code = trxLimit.checkLimit(request.getMsisdn(), request.getCustomerLimitType(),
                trxType, Long.parseLong(request.getAmount()), value, sqlconf);

        System.out.println("RC Check Limit : " + response_code);

        if ("00".equals(response_code)) {
            System.out.println("::: doInquiry Card OTP Microservices Request :::");
            System.out.println(new Gson().toJson(request));

            try {

                HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
                RestTemplate restTemps = new RestTemplate();
                String url = doInquiry;

                System.out.println("doInquiry Card OTP Url : " + url);

                ResponseEntity<doInquiryCardOTPResp> response = restTemps.exchange(url, HttpMethod.POST, req, doInquiryCardOTPResp.class);
                doInquiryCardOTPResp doInquiryCardOTPResp = response.getBody();

                System.out.println("::: doInquiry Card OTP Microservices Response :::");
                System.out.println(new Gson().toJson(response.getBody()));


                mbApiResp = MbJsonUtil.createResponseTrf(doInquiryCardOTPResp.getResponseCode(), doInquiryCardOTPResp.getResponseMessage(), doInquiryCardOTPResp.getResponseContent(), doInquiryCardOTPResp.getTransactionId());


            } catch (Exception e) {
                mbApiResp = MbJsonUtil.createResponseTrf("99",
                        e.toString(),
                        null, "");
                MbLogUtil.writeLogError(log, "99", e.toString());
            }
        } else if ("01".equals(response_code)) {

            if (request.getLanguage().equalsIgnoreCase("en")) {
                response_msg = "You cannot do financial transactions.\nPlease come to our branch to activate it.";
                mbApiResp = MbJsonUtil.createResponseTrf("01",
                        response_msg,
                        null, "");
            } else {
                response_msg = "Anda belum bisa melakukan transaksi finansial.\nSilahkan datang ke cabang untuk mengaktifkannya.";
                mbApiResp = MbJsonUtil.createResponseTrf("01",
                        response_msg,
                        null, "");
            }


        } else if ("02".equals(response_code)) {

            if (request.getLanguage().equalsIgnoreCase("en")) {
                response_msg = "Your transaction has exceeded the limit.";
                mbApiResp = MbJsonUtil.createResponseTrf("02",
                        response_msg,
                        null, "");
            } else {
                response_msg = "Transaksi Anda melebihi limit.";
                mbApiResp = MbJsonUtil.createResponseTrf("02",
                        response_msg,
                        null, "");
            }


        } else {

            if (request.getLanguage().equalsIgnoreCase("en")) {
                mbApiResp = MbJsonUtil.createResponseTrf("03",
                        "Limit Check Failed",
                        null, "");
            } else {
                mbApiResp = MbJsonUtil.createResponseTrf("03",
                        "Check Limit Gagal",
                        null, "");
            }

        }


        txLog.setResponse(mbApiResp);
        txLogRepository.save(txLog);


        return mbApiResp;
    }

}
