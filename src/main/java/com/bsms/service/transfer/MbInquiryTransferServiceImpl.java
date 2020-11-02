package com.bsms.service.transfer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import com.bsms.restobjclient.transfer.ContentInqTrf;
import com.bsms.restobjclient.transfer.InquiryTrfDispResp;
import com.bsms.restobjclient.transfer.InquiryTrfReq;
import com.bsms.restobjclient.transfer.InquiryTrfResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbLogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

//addition by Dodo
import com.bsms.util.TrxIdUtil;
import com.bsms.util.TrxLimit;

@Service("inquiryTransfer")
public class MbInquiryTransferServiceImpl extends MbBaseServiceImpl implements MbService {
    @Value("${sql.conf}")
    private String sqlconf;

    @Value("${core.service.inquiryTransfer}")
    private String inquiryTransfer;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;

    RestTemplate restTemplate = new RestTemplate();

    MbApiResp mbApiResp;

    String accountNumber = "";
    String amount = "";
    String response_msg = null;
    String DestinationAccountNumber = null;

    Client client = ClientBuilder.newClient();

    private static Logger log = LoggerFactory.getLogger(MbInquiryTransferServiceImpl.class);

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {
        LibFunctionUtil libFunct = new LibFunctionUtil();
        String trx_id = libFunct.getTransactionID(6);

        MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);

        //=============== cek limit ================//
        JSONObject value = new JSONObject();
        TrxLimit trxLimit = new TrxLimit();
        int trxType = TrxLimit.TRANSFER;

        String response_code = trxLimit.checkLimit(request.getMsisdn(), request.getCustomerLimitType(),
                trxType, Long.parseLong(request.getAmount()), value, sqlconf);

        System.out.println("RC Check Limit : " + response_code);

        if ("00".equals(response_code)) {

            InquiryTrfReq inquiryTrfReq = new InquiryTrfReq();

            System.out.println("ID Favorit : " + request.getId_favorit());

            if (request.getId_favorit() == null) {
                inquiryTrfReq.setCorrelationId(trx_id);
                inquiryTrfReq.setTransactionId(trx_id);
                inquiryTrfReq.setDeliveryChannel("6027");
                inquiryTrfReq.setSourceAccountNumber(request.getAccount_number());
                inquiryTrfReq.setSourceAccountName(request.getCustomerName());
                inquiryTrfReq.setDestinationAccountNumber(request.getDestinationAccountNumber());
                inquiryTrfReq.setDestinationAccountName("");
                inquiryTrfReq.setAmount(request.getAmount());
                inquiryTrfReq.setDescription(request.getDescription());
                inquiryTrfReq.setPan(request.getPan());
                inquiryTrfReq.setCardAcceptorTerminal("00307180");
                inquiryTrfReq.setCardAcceptorMerchantId(request.getMsisdn());
                inquiryTrfReq.setCurrency("360");

                DestinationAccountNumber = request.getDestinationAccountNumber();
            } else {
                try (Connection con = DriverManager.getConnection(sqlconf);) {
                    Statement stmt;
                    String SQL;

                    //============================= check favorite exist or no =================//
                    stmt = con.createStatement();
                    SQL = "SELECT * from Favorite where id_fav='" + request.getId_favorit() + "'";
                    ResultSet rs = stmt.executeQuery(SQL);

                    if (rs.next()) {
                        inquiryTrfReq.setCorrelationId(trx_id);
                        inquiryTrfReq.setTransactionId(trx_id);
                        inquiryTrfReq.setDeliveryChannel("6027");
                        inquiryTrfReq.setSourceAccountNumber(request.getAccount_number());
                        inquiryTrfReq.setSourceAccountName(request.getCustomerName());
                        inquiryTrfReq.setDestinationAccountNumber(rs.getString("destinationAccountNumber"));
                        inquiryTrfReq.setDestinationAccountName("");
                        inquiryTrfReq.setAmount(request.getAmount());
                        inquiryTrfReq.setDescription(request.getDescription());
                        inquiryTrfReq.setPan(request.getPan());
                        inquiryTrfReq.setCardAcceptorTerminal("00307180");
                        inquiryTrfReq.setCardAcceptorMerchantId(request.getMsisdn());
                        inquiryTrfReq.setCurrency("360");

                        DestinationAccountNumber = rs.getString("destinationAccountNumber");
                    } else {

                        inquiryTrfReq.setCorrelationId(trx_id);
                        inquiryTrfReq.setTransactionId(trx_id);
                        inquiryTrfReq.setDeliveryChannel("6027");
                        inquiryTrfReq.setSourceAccountNumber(request.getAccount_number());
                        inquiryTrfReq.setSourceAccountName(request.getCustomerName());
                        inquiryTrfReq.setDestinationAccountNumber(request.getDestinationAccountNumber());
                        inquiryTrfReq.setDestinationAccountName("");
                        inquiryTrfReq.setAmount(request.getAmount());
                        inquiryTrfReq.setDescription(request.getDescription());
                        inquiryTrfReq.setPan(request.getPan());
                        inquiryTrfReq.setCardAcceptorTerminal("00307180");
                        inquiryTrfReq.setCardAcceptorMerchantId(request.getMsisdn());
                        inquiryTrfReq.setCurrency("360");

                        DestinationAccountNumber = request.getDestinationAccountNumber();
                    }

                    con.close();


                } catch (SQLException e) {

                    MbLogUtil.writeLogError(log, e, e.toString());

                    mbApiResp = MbJsonUtil.createResponseTrf("99",
                            "Inquiry Transfer Failed",
                            null, "");

                }
            }


            System.out.println(new Gson().toJson(inquiryTrfReq));

            try {

                HttpEntity<?> req = new HttpEntity(inquiryTrfReq, RestUtil.getHeaders());
                RestTemplate restTemps = new RestTemplate();
                String url = inquiryTransfer;

                ResponseEntity<InquiryTrfResp> response = restTemps.exchange(url, HttpMethod.POST, req, InquiryTrfResp.class);
                InquiryTrfResp inquiryTrfResp = response.getBody();

                System.out.println("::: Inquiry Trf From Back End :::");
                System.out.println(new Gson().toJson(response.getBody()));


                if ("00".equals(inquiryTrfResp.getResponseCode())) {

                    List<ContentInqTrf> content = new ArrayList<>();
                    if(request.getLanguage().equals("en")){
                        content.add(new ContentInqTrf("Amount", request.getAmount()));
                        content.add(new ContentInqTrf("Description", request.getDescription()));

                    }else{
                        content.add(new ContentInqTrf("Jumlah", request.getAmount()));
                        content.add(new ContentInqTrf("Keterangan", request.getDescription()));
                    }

                    InquiryTrfDispResp inquiryTrfDispResp = new InquiryTrfDispResp(request.getAccount_number(),
                            request.getCustomerName(),
                            DestinationAccountNumber,
                            inquiryTrfResp.getContent().getDestinationAccountName(),
                            content, trx_id);

                    mbApiResp = MbJsonUtil.createResponseTrf(inquiryTrfResp.getResponseCode(),
                            "Success",
                            inquiryTrfDispResp, trx_id);


                } else {
                    System.out.println(inquiryTrfResp.getResponseCode() + " <<<========== response code error");
                    mbApiResp = MbJsonUtil.createResponseTrf(inquiryTrfResp.getResponseCode(),
                            inquiryTrfResp.getContent().getErrorMessage(),
                            null, "");


                }
            } catch (Exception e) {
                mbApiResp = MbJsonUtil.createResponseTrf("99",
                        e.toString(),
                        null, "");
                MbLogUtil.writeLogError(log, "99", e.toString());

            }
        } else if ("01".equals(response_code)) {

            response_msg = request.getLanguage().equals("id") ? "Anda belum bisa melakukan transaksi finansial.\nSilahkan datang ke cabang untuk mengaktifkannya." : "Sorry, we can not process your transaction at this moment. Please come to the branch to activate";
            mbApiResp = MbJsonUtil.createResponseTrf("01",
                    response_msg,
                    null, "");

        } else if ("02".equals(response_code)) {

            response_msg = request.getLanguage().equals("id") ? "Transaksi Anda melebihi limit." : "You have achieved maximum limit of transaction.";
            mbApiResp = MbJsonUtil.createResponseTrf("02",
                    response_msg,
                    null, "");

        } else {
            mbApiResp = MbJsonUtil.createResponseTrf("03",
                    request.getLanguage().equals("id")?"Check Limit Gagal":"Limit Check Failed",
                    null, "");
        }

        txLog.setResponse(mbApiResp);
        txLogRepository.save(txLog);

        return mbApiResp;
    }

}
