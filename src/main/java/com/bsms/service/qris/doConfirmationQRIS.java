package com.bsms.service.qris;

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
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.emoney.doInquiryEmoneyResp;
import com.bsms.restobjclient.limit.InfoLimitDispResp;
import com.bsms.restobjclient.qris.doConfirmationQRISResp;
import com.bsms.restobjclient.qris.doInquiryQRISResp;
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

@Service("doConfirmationQRIS")
public class doConfirmationQRIS extends MbBaseServiceImpl implements MbService {

    @Value("${sql.conf}")
    private String sqlconf;

    @Value("${qris.doConfirmation}")
    private String doConfirmation;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;

    RestTemplate restTemplate = new RestTemplate();

    MbApiResp mbApiResp;

    Client client = ClientBuilder.newClient();

    private static Logger log = LoggerFactory.getLogger(MbInquiryOnlineTrfService.class);


    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {

        MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);

        System.out.println("::: doConfirmation QRIS Microservices Request :::");
        System.out.println(new Gson().toJson(request));

        //=============== cek limit ================//
        String response_msg = null;

        JSONObject value = new JSONObject();
        TrxLimit trxLimit = new TrxLimit();
        int trxType = TrxLimit.QRIS;

        //double d=Double.parseDouble(request.getAmount());
        double QRISAmount = Double.parseDouble(request.getAmount()); //transaction amount
        double QRISTips = Double.parseDouble(request.getTips()); //tips amount

        double d = QRISAmount + QRISTips; //check limits with tips
        long amount_convert = (new Double(d)).longValue(); //129

//        String response_code = trxLimit.checkLimit(request.getMsisdn(), request.getCustomerLimitType(),
//                trxType, amount_convert, value, sqlconf);
        
        String trxAmt=String.valueOf(d);  
        String response_code = checklimitTransaction(trxAmt, request.getCustomerLimitType(), 
        		request.getMsisdn(), TrxLimit.QRIS, request.getLanguage());

        System.out.println("RC Check Limit : " + response_code);

        if ("00".equals(response_code)) {
            try {

                HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());

                RestTemplate restTemps = new RestTemplate();
                String url = doConfirmation;

                ResponseEntity<doConfirmationQRISResp> response = restTemps.exchange(url, HttpMethod.POST, req, doConfirmationQRISResp.class);
                doConfirmationQRISResp doConfirmationQRISResp = response.getBody();

                System.out.println("::: doConfirmation QRIS Microservices Response :::");
                System.out.println(new Gson().toJson(response.getBody()));

                mbApiResp = MbJsonUtil.createResponseTrf(doConfirmationQRISResp.getResponseCode(), doConfirmationQRISResp.getResponseMessage(), doConfirmationQRISResp.getResponseContent(), doConfirmationQRISResp.getTransactionId());


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
