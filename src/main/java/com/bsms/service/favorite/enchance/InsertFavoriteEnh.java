package com.bsms.service.favorite.enchance;

import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.transfer.InquiryTrfReq;
import com.bsms.restobjclient.transfer.InquiryTrfResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.sql.*;


@Service("insertFavorite")
public class InsertFavoriteEnh extends MbBaseServiceImpl implements MbService {
    @Value("${sql.conf}")
    private String connectionUrl;

    @Value("${core.service.inquiryTransfer}")
    private String inquiryTransfer;

    @Value("${core.service.inquiryOnlineTransfer}")
    private String inquiryOnlineTransfer;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;

    RestTemplate restTemplate = new RestTemplate();

    String favSuccessId = "Favorit Berhasil Ditambahkan";
    String favSuccessEn = "Insert Favorite Success";

    String favFailedId = "Favorit Gagal Ditambahkan";
    String favFailedEn = "Insert Favorite Failed";

    String favExistEn = "Favorite Already Exist";
    String favExistId = "Favorite Telah Ada";


    MbApiResp mbApiResp;

    Client client = ClientBuilder.newClient();

    String BankName = "";
    String service_code = "";
    String via_atm = "";
    int feature = 0;
    String response_msg = null;

    static int FEAT_SKN = 0x01;
    static int FEAT_BERSAMA = 0x02;
    static int FEAT_PRIMA = 0x04;
    static int FEAT_RTGS = 0x08;

    private static Logger log = LoggerFactory.getLogger(InsertFavoriteEnh.class);

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {

        System.out.println("insert favorite enchancement");
        LibFunctionUtil libFunct = new LibFunctionUtil();
        String trx_id = libFunct.getTransactionID(6);
        String language = request.getLanguage() != null ? request.getLanguage() : "id";

        MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);

        //========== insert favorite ==============//
        try (Connection con = DriverManager.getConnection(connectionUrl);) {
            PreparedStatement ps, ps2, ps3, ps4, ps5;
            String SQL = null, SQL2 = null, SQL3 = null, SQL4 = null, SQL5 = null;
            String submodul_id, msisdn, DestinationAccountNumber, billkey1, billCode, billerid;

            //============================= check favorite exist or no =================//
            if (request.getId_favorit() == null) {
                submodul_id = request.getSub_modul_id();
                msisdn = request.getMsisdn();
                DestinationAccountNumber = request.getDestinationAccountNumber();

                //Purchase & Payment
                billkey1 = request.getBillkey1();
                billCode = request.getBillCode();
                billerid = request.getBillerid();

            } else {
                log.info("find favorite by id");
//                stmt5 = con.createStatement();
//                SQL5 = "SELECT * from Favorite where id_fav='" + request.getId_favorit() + "'";
                SQL5 = "SELECT * from Favorite where id_fav=?";

                ps5 = con.prepareStatement(SQL5);
                ps5.setString(1, request.getId_favorit());

                ResultSet rs5 = ps5.executeQuery();

                if (rs5.next()) {
                    System.out.println("data select ada");
                    submodul_id = rs5.getString("submodul_id");
                    msisdn = rs5.getString("msisdn");
                    DestinationAccountNumber = rs5.getString("destinationAccountNumber");

                    //Purchase & Payment
                    billkey1 = rs5.getString("billkey1");
                    billCode = rs5.getString("billCode");
                    billerid = rs5.getString("billerid");
                } else {
                    System.out.println("data select tidak ada ");
                    submodul_id = request.getSub_modul_id();
                    msisdn = request.getMsisdn();
                    DestinationAccountNumber = request.getDestinationAccountNumber();

                    //Purchase & Payment
                    billkey1 = request.getBillkey1();
                    billCode = request.getBillCode();
                    billerid = request.getBillerid();
                }
                rs5.close();
                ps5.close();
            }

            System.out.println(submodul_id);
            System.out.println(msisdn);
            System.out.println(DestinationAccountNumber);

            //Purchase & Payment
            System.out.println(billkey1);
            System.out.println(billCode);
            System.out.println(billerid);

//            stmt = con.createStatement();
            int status = 0;

            if (submodul_id.substring(0, 2).equalsIgnoreCase("TR")) {
//                SQL = "SELECT count(id) as id from Favorite where submodul_id='" + submodul_id + "' "
//                        + "and msisdn='" + msisdn + "' and destinationAccountNumber='" + DestinationAccountNumber + "'";
                SQL = "SELECT count(id) as id from Favorite where submodul_id=?and msisdn=? and destinationAccountNumber=?";

                ps = con.prepareStatement(SQL);
                ps.setString(1, request.getSub_modul_id());
                ps.setString(2, request.getMsisdn());
                ps.setString(3, request.getDestinationAccountNumber());

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println(rs.getInt("id"));
                    status = rs.getInt("id");
                }
                rs.close();
                ps.close();
            } else if (submodul_id.substring(0, 2).equalsIgnoreCase("PU") || submodul_id.substring(0, 2).equalsIgnoreCase("PY")) {
                SQL = "SELECT count(id) as id from Favorite where submodul_id=? and msisdn=? and billkey1=?";

                ps = con.prepareStatement(SQL);
                ps.setString(1, request.getSub_modul_id());
                ps.setString(2, request.getMsisdn());
                ps.setString(3, request.getBillkey1());

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println(rs.getInt("id"));
                    status = rs.getInt("id");
                }
                rs.close();
                ps.close();
            }

            //============================= end check favorite exist or no =================//

            System.out.println("Status Check : " + status + " // Fav Title : " + request.getFav_title());

            if (status == 0) {
                if (request.getSub_modul_id().equalsIgnoreCase("TR01")) {
                    System.out.println("Internal Trf");
                    InquiryTrfReq inquiryTrfReq = new InquiryTrfReq();

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
//                            stmt2 = con.createStatement();
//                            SQL2 = "insert into Favorite(id_fav,created,fav_title,msisdn,submodul_id,destinationAccountNumber,destinationAccountName,bankName) "
//                                    + "values('" + trx_id + "',GETDATE(),'" + request.getFav_title() + "','" + request.getMsisdn() + "','" + request.getSub_modul_id() + "',"
//                                    + "'" + request.getDestinationAccountNumber() + "','" + inquiryTrfResp.getContent().getDestinationAccountName() + "','BSI')";
                            SQL2 = "insert into Favorite(id_fav,created,fav_title,msisdn,submodul_id,destinationAccountNumber,destinationAccountName,bankName) "
                                    + "values(?,GETDATE(),?,?,?,?,?,'BSI')";

                            ps2 = con.prepareStatement(SQL2);

                            ps2.setString(1, trx_id);
                            ps2.setString(2, request.getFav_title());
                            ps2.setString(3, request.getMsisdn());
                            ps2.setString(4, request.getSub_modul_id());
                            ps2.setString(5, request.getDestinationAccountNumber());
                            ps2.setString(6, request.getDestinationAccountName());

                            ps2.executeUpdate();
                            ps2.close();

                            mbApiResp = MbJsonUtil.createResponseTrf("00",
                                    language.equals("en") ? favSuccessEn : favSuccessId,
                                    null, "");
                        } else {
                            mbApiResp = MbJsonUtil.createResponseTrf("99",
                                    language.equals("en") ? favFailedEn : favFailedId,
                                    null, "");
                        }
                    } catch (Exception e) {
                        mbApiResp = MbJsonUtil.createResponseTrf("99",
                                language.equals("en") ? favFailedEn : favFailedId,
                                null, "");
                        MbLogUtil.writeLogError(log, "99", e.toString());
                    }
                } else if (request.getSub_modul_id().equalsIgnoreCase("TR02")) {
                    System.out.println("Online Trf");
                    //========== get Bank Data ==============//
//                    stmt3 = con.createStatement();
                    //update typeBank field by Dwi S
//                    SQL3 = "SELECT Code, Jenis,Feature, Name FROM Banks with (NOLOCK) INNER JOIN "
//                            + "BankPrior ON Code = IdBank where Code ='" + request.getDestinationBank() + "' and Jenis='" + request.getTypeBank() + "'";

                    SQL3 = "SELECT Code, Jenis,Feature, Name FROM Banks with (NOLOCK) INNER JOIN "
                            + "BankPrior ON Code = IdBank where Code =? and Jenis=?";

                    ps3 = con.prepareStatement(SQL3);
                    ps3.setString(1, request.getDestinationBank());
                    ps3.setString(2, request.getTypeBank());

                    ResultSet rs3 = ps3.executeQuery();

                    while (rs3.next()) {
                        BankName = rs3.getString("Name");
                        feature = Integer.parseInt(rs3.getString("Feature"));
                    }

                    //========== define service code ==============//
                    if (((feature & FEAT_PRIMA) == FEAT_PRIMA)) {
                        // ATM Prima
                        service_code = "0500";
                        via_atm = "Prima";
                    } else if (((feature & FEAT_BERSAMA) == FEAT_BERSAMA)) {
                        // ATM Bersama
                        service_code = "0200";
                        via_atm = "ATM Bersama";
                    } else {
                        // ATM Prima by Default
                        service_code = "0500";
                        via_atm = "Prima";
                    }

                    InquiryTrfReq inquiryTrfReq = new InquiryTrfReq();
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
                    inquiryTrfReq.setCardAcceptorTerminal("00307181");
                    inquiryTrfReq.setCardAcceptorMerchantId(request.getMsisdn());
                    inquiryTrfReq.setCurrency("360");
                    inquiryTrfReq.setBeneficiaryInstitutionCode(request.getDestinationBank());
                    inquiryTrfReq.setServiceCode(service_code);
                    inquiryTrfReq.setReferenceNumber(request.getRef_no());
                    inquiryTrfReq.setTypeBank(request.getTypeBank());

                    System.out.println("::: Inquiry Trf Online Request to Back End :::");
                    System.out.println(new Gson().toJson(inquiryTrfReq));

                    try {

                        HttpEntity<?> req = new HttpEntity(inquiryTrfReq, RestUtil.getHeaders());
                        RestTemplate restTemps = new RestTemplate();
                        String url = inquiryOnlineTransfer;

                        ResponseEntity<InquiryTrfResp> response = restTemps.exchange(url, HttpMethod.POST, req, InquiryTrfResp.class);
                        InquiryTrfResp inquiryTrfResp = response.getBody();

                        System.out.println("::: Inquiry Trf Online Response From Back End :::");
                        System.out.println(new Gson().toJson(response.getBody()));

                        if ("00".equals(inquiryTrfResp.getResponseCode())) {
//                            stmt4 = con.createStatement();
//                            SQL4 = "insert into Favorite(id_fav,created,fav_title,msisdn,submodul_id,destinationAccountNumber,"
//                                    + "destinationBank,destinationAccountName,bankName,trfMethod, typeBank) "
//                                    + "values('" + trx_id + "',GETDATE(),'" + request.getFav_title() + "','" + request.getMsisdn() + "','" + request.getSub_modul_id() + "',"
//                                    + "'" + request.getDestinationAccountNumber() + "','" + request.getDestinationBank() + "',"
//                                    + "'" + inquiryTrfResp.getContent().getDestinationAccountName() + "','" + BankName + "',"
//                                    + "" + request.getTrf_method() + "," + request.getTypeBank() + ")";
                            SQL4 = "insert into Favorite(id_fav,created,fav_title,msisdn,submodul_id,destinationAccountNumber,destinationBank,destinationAccountName,bankName,trfMethod,typeBank) "
                                    + "values(?,GETDATE(),?,?,?,?,?,?,?,?,? )";

                            ps4 = con.prepareStatement(SQL4);
                            ps4.setString(1, trx_id);
                            ps4.setString(2, request.getFav_title());
                            ps4.setString(3, request.getMsisdn());
                            ps4.setString(4, request.getSub_modul_id());
                            ps4.setString(5, request.getDestinationAccountNumber());
                            ps4.setString(6, request.getDestinationBank());
                            ps4.setString(7, inquiryTrfResp.getContent().getDestinationAccountName());
                            ps4.setString(8, BankName);
                            ps4.setString(9, request.getTrf_method());
                            ps4.setString(10, request.getTypeBank());

                            ps4.executeUpdate();
                            ps4.close();

                            mbApiResp = MbJsonUtil.createResponseTrf("00",
                                    language.equals("en") ? favSuccessEn : favSuccessId,
                                    null, "");
                        } else {
                            mbApiResp = MbJsonUtil.createResponseTrf("99",
                                    language.equals("en") ? favFailedEn : favFailedId,
                                    null, "");

                        }
                    } catch (Exception e) {
                        mbApiResp = MbJsonUtil.createResponseTrf("99",
                                language.equals("en") ? favFailedEn : favFailedId,
                                null, "");
                        MbLogUtil.writeLogError(log, "99", e.toString());
                    }


                } else if (submodul_id.substring(0, 2).equalsIgnoreCase("PU") || submodul_id.substring(0, 2).equalsIgnoreCase("PY")) {
                    System.out.println("::: Favorite Purchase & Payment :::");

                    String billingKey = billkey1 != null ? billkey1 : request.getPayment_id();

//                    stmt2 = con.createStatement();
//
//                    SQL2 = "insert into Favorite(id_fav,created,fav_title,msisdn,submodul_id,billkey1,billCode,billerid) "
//                            + "values('" + trx_id + "',GETDATE(),'" + request.getFav_title() + "','" + request.getMsisdn() + "','" + request.getSub_modul_id() + "',"
//                            + "'" + billingKey + "','" + billCode + "','" + billerid + "')";

                    SQL2 = "insert into Favorite(id_fav,created,fav_title,msisdn,submodul_id,billkey1,billCode,billerid) "
                            + "values(?,GETDATE(),?,?,?,?,?,?)";

                    ps2 = con.prepareStatement(SQL2);
                    ps2.setString(1, trx_id);
                    ps2.setString(2, request.getFav_title());
                    ps2.setString(3, request.getMsisdn());
                    ps2.setString(4, request.getSub_modul_id());
                    ps2.setString(5, billingKey);
                    ps2.setString(6, billCode);
                    ps2.setString(7, billerid);

                    ps2.executeUpdate();
                    ps2.close();

                    mbApiResp = MbJsonUtil.createResponseTrf("00",
                            request.getLanguage().equals("en") ? "Insert Favorite Success" : "Favorit telah dimasukkan.",
                            null, "");
                }
            } else {
                mbApiResp = MbJsonUtil.createResponseTrf("01",
                        language.equals("en") ? favExistEn : favExistId,
                        null, "");
            }
            con.close();
        } catch (SQLException e) {
            MbLogUtil.writeLogError(log, e, e.toString());

            mbApiResp = MbJsonUtil.createResponseTrf("99",
                    language.equals("en") ? favFailedEn : favFailedId,
                    null, "");
        }
        txLog.setResponse(mbApiResp);
        txLogRepository.save(txLog);
        return mbApiResp;
    }

}
