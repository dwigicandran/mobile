package com.bsms.service.transfer;

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

import com.bsms.util.*;
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
import com.bsms.restobjclient.transfer.Bank;
import com.bsms.restobjclient.transfer.BankDispResp;
import com.bsms.restobjclient.transfer.ContentInqTrf;
import com.bsms.restobjclient.transfer.InquiryTrfDispResp;
import com.bsms.restobjclient.transfer.InquiryTrfReq;
import com.bsms.restobjclient.transfer.InquiryTrfResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("inquiryOnlineTransfer")
public class MbInquiryOnlineTrfService extends MbBaseServiceImpl implements MbService {
    @Value("${sql.conf}")
    private String sqlconf;

    static int FEAT_SKN = 0x01;
    static int FEAT_BERSAMA = 0x02;
    static int FEAT_PRIMA = 0x04;
    static int FEAT_RTGS = 0x08;

    @Value("${sql.conf}")
    private String connectionUrl;

    @Value("${core.service.inquiryOnlineTransfer}")
    private String inquiryOnlineTransfer;

    @Value("${core.service.inquirySknTransfer}")
    private String inquirySknTransfer;

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

        String BankName = "";
        String service_code = "";
        String via_atm = "";
        int feature = 0;
        String response_msg = null;
        String DestinationBank = null;
        String DestinationAccountNumber = null;
        String trf_method = null;
        String bankType = null;


        //=============== cek limit ================//
        JSONObject value = new JSONObject();
        TrxLimit trxLimit = new TrxLimit();

        int trxType = request.getTrf_method().equalsIgnoreCase("1") ? TrxLimit.TRANSFER_ONLINE : TrxLimit.TRANSFER_SKN;
//        int trxType = TrxLimit.TRANSFER_ONLINE;

        log.info("Transfer melalui : " + trxType);


//        String response_code = trxLimit.checkLimit(request.getMsisdn(), request.getCustomerLimitType(),
//                trxType, Long.parseLong(request.getAmount()), value, sqlconf);
        
        String response_code = checklimitTransaction(request.getAmount(), request.getCustomerLimitType(), 
        		request.getMsisdn(), trxType, request.getLanguage());

        System.out.println("RC Check Limit : " + response_code);


        if ("00".equals(response_code)) {

            System.out.println("ID Favorit : " + request.getId_favorit());


            if (request.getId_favorit() == null) {
                DestinationBank = request.getDestinationBank();
                bankType = request.getTypeBank();

                DestinationAccountNumber = request.getDestinationAccountNumber();
                trf_method = request.getTrf_method();
            } else {

                try (Connection con = DriverManager.getConnection(sqlconf);) {
                    Statement stmt;
                    String SQL;

                    //============================= check favorite exist or no =================//
                    stmt = con.createStatement();
                    SQL = "SELECT * from Favorite where id_fav='" + request.getId_favorit() + "'";
                    ResultSet rs = stmt.executeQuery(SQL);

                    if (rs.next()) {
                        DestinationBank = rs.getString("destinationBank");
                        DestinationAccountNumber = rs.getString("destinationAccountNumber");
                        trf_method = rs.getString("trfMethod");
                        bankType = rs.getString("typeBank");
                    } else {
                        DestinationBank = request.getDestinationBank();
                        DestinationAccountNumber = request.getDestinationAccountNumber();
                        trf_method = request.getTrf_method();
                    }

                    con.close();


                } catch (SQLException e) {

                    MbLogUtil.writeLogError(log, e, e.toString());

                    mbApiResp = MbJsonUtil.createResponseTrf("99",
                            "Inquiry Transfer Failed",
                            null, "");

                }
            }

            //========== get Bank Data ==============//
            try (Connection con = DriverManager.getConnection(connectionUrl);) {
                Statement stmt;
                String SQL;

//                log.info("banktype : " + bankType);

                stmt = con.createStatement();
                SQL = "SELECT Code, Jenis,Feature, Name FROM Banks with (NOLOCK) INNER JOIN "
                        + "BankPrior ON Code = IdBank where Code ='" + DestinationBank + "' and Jenis='" + bankType + "'";
                ResultSet rs = stmt.executeQuery(SQL);

                while (rs.next()) {
                    BankName = rs.getString("Name");
                    feature = Integer.parseInt(rs.getString("Feature"));
                }

                rs.close();
                stmt.close();
                con.close();


            } catch (SQLException e) {

                MbLogUtil.writeLogError(log, e, e.toString());

            }

            log.info("feature" + feature);

            //========== define service code ==============//

            if (trf_method.equalsIgnoreCase("1")) {
                if (((feature & FEAT_PRIMA) == FEAT_PRIMA)) {
                    // ATM Prima
                    log.info("prima");
                    service_code = "0500";
                    via_atm = "Prima";
                } else if (((feature & FEAT_BERSAMA) == FEAT_BERSAMA)) {
                    // ATM Bersama
                    log.info("bersama");
                    service_code = "0200";
                    via_atm = "ATM Bersama";
                } else {
                    // ATM Prima by Default
                    log.info("else transfer");
                    service_code = "0500";
                    via_atm = "Prima";
                }
            } else {
                log.info("transfer skn method ");
                service_code = "0400";
            }


            LibFunctionUtil libFunct = new LibFunctionUtil();
            String trx_id = libFunct.getTransactionID(6);

            MbApiTxLog txLog = new MbApiTxLog();
            txLogRepository.save(txLog);

            InquiryTrfReq inquiryTrfReq = new InquiryTrfReq();
            inquiryTrfReq.setCorrelationId(trx_id);
            inquiryTrfReq.setTransactionId(trx_id);
            inquiryTrfReq.setDeliveryChannel("6027");
            inquiryTrfReq.setSourceAccountNumber(request.getAccount_number());
            inquiryTrfReq.setSourceAccountName(request.getCustomerName());
            inquiryTrfReq.setDestinationAccountNumber(DestinationAccountNumber);
            inquiryTrfReq.setDestinationAccountName("");
            inquiryTrfReq.setAmount(request.getAmount());
            inquiryTrfReq.setDescription(request.getDescription());
            inquiryTrfReq.setPan(request.getPan());
            inquiryTrfReq.setCardAcceptorTerminal("00307181");
            inquiryTrfReq.setCardAcceptorMerchantId(request.getMsisdn());
            inquiryTrfReq.setCurrency("360");
            inquiryTrfReq.setBeneficiaryInstitutionCode(DestinationBank);
            inquiryTrfReq.setServiceCode(service_code);
            inquiryTrfReq.setReferenceNumber(request.getRef_no());
            inquiryTrfReq.setLanguage(request.getLanguage());

            System.out.println("::: Inquiry Trf Online Request to Back End :::");
            System.out.println(new Gson().toJson(inquiryTrfReq));

            try {

                HttpEntity<?> req = new HttpEntity(inquiryTrfReq, RestUtil.getHeaders());
                RestTemplate restTemps = new RestTemplate();
                String url = service_code.equalsIgnoreCase("0400") ? inquirySknTransfer : inquiryOnlineTransfer;
//                String url = inquiryOnlineTransfer;
                System.out.println("url : " + url);

                ResponseEntity<InquiryTrfResp> response = restTemps.exchange(url, HttpMethod.POST, req, InquiryTrfResp.class);
                InquiryTrfResp inquiryTrfResp = response.getBody();


                System.out.println("::: Inquiry Trf Online Response From Back End :::");
                System.out.println(new Gson().toJson(response.getBody()));

                if ("00".equals(inquiryTrfResp.getResponseCode())) {
                    String info = null;
                    if (trf_method.equalsIgnoreCase("1")) {
                        trf_method = "Online";
                    } else {
                        trf_method = "SKN";
                        System.out.println("transfer language : " + request.getLanguage());
                        //Your SKN Transfer Transaction will be processed in a maximum of 1 working day, the SKN process starts at 06:30-14.30 WIB every working day.
                        info = request.getLanguage().equalsIgnoreCase("id") ? "Transaksi SKN anda akan di proses maksimal 1 hari kerja,  proses SKN mulai pukul 06:30 ; 14:30 WIB setiap hari kerja." : "Your SKN Transfer Transaction will be processed in a maximum of 1 working day, the SKN process starts at 06:30-14.30 WIB every working day.";
                    }

                    List<ContentInqTrf> content = new ArrayList<>();
                    if (request.getLanguage().equals("en")) {
                        content.add(new ContentInqTrf("Bank Destination", BankName));
                        content.add(new ContentInqTrf("Transfer Method", trf_method));
                        content.add(new ContentInqTrf("Amount", TextUtil.decimalFormater(request.getAmount())));
                        content.add(new ContentInqTrf("Description", request.getDescription()));
                        content.add(new ContentInqTrf("Referrence Number", request.getRef_no()));
                    } else { //penambahan content bahasa indo oleh Dwi
                        content.add(new ContentInqTrf("Bank Tujuan", BankName));
                        content.add(new ContentInqTrf("Metode Transfer", trf_method));
                        content.add(new ContentInqTrf("Jumlah", TextUtil.decimalFormater(request.getAmount())));
                        content.add(new ContentInqTrf("Keterangan", request.getDescription()));
                        content.add(new ContentInqTrf("No Referensi", request.getRef_no()));
                    }


                    InquiryTrfDispResp inquiryTrfDispResp = new InquiryTrfDispResp(request.getAccount_number(),
                            request.getCustomerName(),
                            DestinationAccountNumber,
                            inquiryTrfResp.getContent().getDestinationAccountName(),
                            content, trx_id, info);


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

            txLog.setResponse(mbApiResp);
            txLogRepository.save(txLog);
        } else if ("01".equals(response_code)) {

            response_msg = request.getLanguage().equals("id") ? "Anda belum bisa melakukan transaksi finansial.\nSilahkan datang ke cabang untuk mengaktifkannya." : "Sorry, we can not process your transaction at this moment. Please contact BSM Customer Service or BSM Call Center";
            mbApiResp = MbJsonUtil.createResponseTrf("01",
                    response_msg,
                    null, "");

        } else if ("02".equals(response_code)) {

            response_msg = request.getLanguage().equals("id") ? "Transaksi Anda melebihi limit." : "You have achieved maximum amount of transaction.";
            mbApiResp = MbJsonUtil.createResponseTrf("02",
                    response_msg,
                    null, "");

        } else {

            mbApiResp = MbJsonUtil.createResponseTrf("03",
                    request.getLanguage().equals("id") ? "Check Limit Gagal" : "Limit Check Failed",
                    null, "");
        }


        return mbApiResp;
    }

}
