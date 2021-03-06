package com.bsms.service.transfer;

import java.io.BufferedInputStream;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.transfer.ContentIntTrf;
import com.bsms.restobjclient.transfer.InquiryTrfReq;
import com.bsms.restobjclient.transfer.InquiryTrfResp;
import com.bsms.restobjclient.transfer.OnlineTrfDispResp;
import com.bsms.restobjclient.transfer.OnlineTrfResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("onlineTransfer")
public class MbOnlineTransferServiceImpl extends MbBaseServiceImpl implements MbService {
    static int FEAT_SKN = 0x01;
    static int FEAT_BERSAMA = 0x02;
    static int FEAT_PRIMA = 0x04;
    static int FEAT_RTGS = 0x08;

    @Value("${sql.conf}")
    private String connectionUrl;

    @Value("${core.service.inquiryOnlineTransfer}")
    private String inquiryTransferOnline;

    @Value("${core.service.inquirySknTransfer}")
    private String inquirySknTransferOnline;

    @Value("${core.service.onlineTransfer}")
    private String onlineTransfer;

    @Value("${core.service.SKNTransfer}")
    private String sknTransfer;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;

    @Value("${fee.bersama}")
    private double fee_bersama;

    @Value("${fee.prima}")
    private double fee_prima;

    @Value("${fee.skn}")
    private double fee_skn;

    @Value("${template.trf.online}")
    private String templateTRF;

    @Value("${template.logo}")
    private String templateLogo;

    @Value("${tmp.folder}")
    private static String tmp_folder;


    RestTemplate restTemplate = new RestTemplate();

    MbApiResp mbApiResp;

    Double amount, fee_admin;
    String amount_display, date_trx, amount_display_admin;

    Client client = ClientBuilder.newClient();

    private static Logger log = LoggerFactory.getLogger(MbOnlineTransferServiceImpl.class);

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {
        String BankName = "";
        String service_code = "";
        String via_atm = "";
        int feature = 0;

        String DestinationBank = null;
        String DestinationAccountNumber = null;
        String trf_method = null;
        String bankType = null;

        System.out.println("ID Favorit : " + request.getId_favorit());

        if (request.getId_favorit() == null) {
            DestinationBank = request.getDestinationBank();
            DestinationAccountNumber = request.getDestinationAccountNumber();
            bankType = request.getTypeBank();
            trf_method = request.getTrf_method();
        } else {
            try (Connection con = DriverManager.getConnection(connectionUrl);) {
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
                    bankType = request.getTypeBank();
                }

                con.close();


            } catch (SQLException e) {

                MbLogUtil.writeLogError(log, e, e.toString());

                mbApiResp = MbJsonUtil.createResponseTrf("99",
                        "Online Transfer Failed",
                        null, "");

            }
        }

        //========== get Bank Data ==============//
        try (Connection con = DriverManager.getConnection(connectionUrl)) {
            Statement stmt;
            String SQL;

            log.info("bank type : " + bankType);

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

        //========== define service code ==============//

        if (trf_method.equalsIgnoreCase("1")) {
            if (((feature & FEAT_PRIMA) == FEAT_PRIMA)) {
                // ATM Prima
                service_code = "0500";
                via_atm = "Prima";
                fee_admin = fee_prima;
            } else if (((feature & FEAT_BERSAMA) == FEAT_BERSAMA)) {
                // ATM Bersama
                service_code = "0200";
                via_atm = "ATM Bersama";
                fee_admin = fee_bersama;
            } else if (trf_method.equalsIgnoreCase("2")) {
                // SKN
                service_code = "0400";
                via_atm = "SNK";
                fee_admin = fee_skn;
            } else {
                // ATM Prima by Default
                service_code = "0500";
                via_atm = "Prima";
                fee_admin = fee_prima;
            }
        } else {
            service_code = "0400";
            via_atm = "SNK";
            fee_admin = fee_skn;
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
        inquiryTrfReq.setLanguage(request.getLanguage()); //adition by dwi

        System.out.println(new Gson().toJson(inquiryTrfReq));

        try {
            //=========== Inquiry Trf Online ============//
            HttpEntity<?> req = new HttpEntity(inquiryTrfReq, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            String url = service_code.equalsIgnoreCase("0400") ? inquirySknTransferOnline : inquiryTransferOnline;
            ResponseEntity<InquiryTrfResp> response = restTemps.exchange(url, HttpMethod.POST, req, InquiryTrfResp.class);
            InquiryTrfResp inquiryTrfResp = response.getBody();

            System.out.println("::: Inquiry Online Trf From Back End :::");
            System.out.println(new Gson().toJson(response.getBody()));

            String destinationAccName = response.getBody().getContent().getDestinationAccountName() != null ? response.getBody().getContent().getDestinationAccountName() : request.getDestinationAccountName() != null ? request.getDestinationAccountName() : "";
            inquiryTrfReq.setDestinationAccountName(destinationAccName);

            //=========== Internal Trf ============//
            req = new HttpEntity(inquiryTrfReq, RestUtil.getHeaders());
            restTemps = new RestTemplate();
            url = service_code.equalsIgnoreCase("0400") ? sknTransfer : onlineTransfer;


            System.out.println(":: Online Trf Request To Back End ::");
            System.out.println(new Gson().toJson(req));


            ResponseEntity<OnlineTrfResp> response_trf = restTemps.exchange(url, HttpMethod.POST, req, OnlineTrfResp.class);
            OnlineTrfResp OnlineTrfResp = response_trf.getBody();

            System.out.println("::: Online Trf Process From Back End :::");
            System.out.println(new Gson().toJson(response_trf.getBody()));

            if ("00".equals(OnlineTrfResp.getResponseCode())) {

                JSONObject value = new JSONObject();
                TrxLimit trxLimit = new TrxLimit();
                int trxType = service_code.equalsIgnoreCase("0400") ? TrxLimit.TRANSFER_SKN : TrxLimit.TRANSFER_ONLINE;
                updLimitTrx(request.getMsisdn(), request.getCustomerLimitType(),
                        trxType, Long.parseLong(request.getAmount()));

                amount = Double.parseDouble(request.getAmount());
                amount_display = libFunct.formatIDRCurrency(amount);
                amount_display_admin = libFunct.formatIDRCurrency(fee_admin);
                date_trx = LibFunctionUtil.getDatetime("dd/MM/yyyy HH:mm:ss");

                String title = service_code.equalsIgnoreCase("0400") ? "Transfer Ke Bank Lain melalui SKN" : "Transfer Ke Non BSI";
                List<ContentIntTrf> content = new ArrayList<>();

                if (inquiryTrfReq.getLanguage().equals("id")) {
                    content.add(new ContentIntTrf("Status Transaksi", "Berhasil", ""));
                    content.add(new ContentIntTrf("Dari Rekening", TextUtil.maskString(request.getAccount_number(), 0, 6, 'X') + " - Bank Syariah Indonesia", request.getCustomerName()));
                    content.add(new ContentIntTrf("Ke Rekening", DestinationAccountNumber + " - " + BankName, inquiryTrfResp.getContent().getDestinationAccountName()));
                    content.add(new ContentIntTrf("Jumlah", amount_display, ""));
                    content.add(new ContentIntTrf("Biaya Admin", amount_display_admin, ""));
                    content.add(new ContentIntTrf("Keterangan", request.getDescription(), ""));
                    content.add(new ContentIntTrf("No Referensi", request.getRef_no(), ""));
                } else {  // penambahan receipt bahasa inggris by Dwi
                    content.add(new ContentIntTrf("Transaction Status", "Successful", ""));
                    content.add(new ContentIntTrf("From Account", TextUtil.maskString(request.getAccount_number(), 0, 6, 'X') + " - Bank Syariah Indonesia", request.getCustomerName()));
                    content.add(new ContentIntTrf("To Account", DestinationAccountNumber + " - " + BankName, inquiryTrfResp.getContent().getDestinationAccountName()));
                    content.add(new ContentIntTrf("Total", amount_display, ""));
                    content.add(new ContentIntTrf("Admin Fee", amount_display_admin, ""));
                    content.add(new ContentIntTrf("Description", request.getDescription(), ""));
                    content.add(new ContentIntTrf("Referrence Number", request.getRef_no(), ""));
                    title = service_code.equalsIgnoreCase("0400") ? "Transfer To Other Bank via SKN" : "Transfer To Non BSI";
                }

                String info = request.getLanguage().equalsIgnoreCase("en") ? "Thank you for using BSI Mobile, may our services will bring blessing to you." : "Terimakasih telah menggunakan layanan BSI Mobile, semoga layanan kami mendatangkan berkah bagi Anda.";


                OnlineTrfDispResp onlineTrfDispResp = new OnlineTrfDispResp(OnlineTrfResp.getContent().getcbsRefCode(),
                        date_trx,
                        title,
                        info,
                        content);

                mbApiResp = MbJsonUtil.createResponseTrf(OnlineTrfResp.getResponseCode(),
                        "Success",
                        onlineTrfDispResp, trx_id);

                LibFunctionUtil libFunction = new LibFunctionUtil();

                boolean landscape = false;
                String template = null;
                String templateTrf = null;
                String email;

                email = request.getCustomerEmail();

                if (!service_code.equalsIgnoreCase("0400")) {
                    BufferedInputStream bis = new BufferedInputStream(new ClassPathResource(templateTRF).getInputStream());
                    byte[] buffer = new byte[bis.available()];
                    bis.read(buffer, 0, buffer.length);
                    bis.close();

                    templateTrf = new String(buffer);
                    templateTrf = templateTrf.replace("{image}", new ClassPathResource(templateLogo).getURL().toString());

                    templateTrf = templateTrf.replace("{via_atm}", via_atm);
                    templateTrf = templateTrf.replace("{status}", "BERHASIL");

                    templateTrf = templateTrf.replace("{trans_ref}", OnlineTrfResp.getContent().getcbsRefCode());
                    templateTrf = templateTrf.replace("{struck}", OnlineTrfResp.getCorrelationId());
                    templateTrf = templateTrf.replace("{terminal}", TextUtil.maskString(request.getMsisdn(), 0, 8, 'X'));
                    templateTrf = templateTrf.replace("{date_time}", date_trx);

                    templateTrf = templateTrf.replace("{name}", request.getCustomerName());
                    templateTrf = templateTrf.replace("{account}", TextUtil.maskString(request.getAccount_number(), 0, 6, 'X'));

                    templateTrf = templateTrf.replace("{payment_id}", DestinationAccountNumber);
                    templateTrf = templateTrf.replace("{code_name}", BankName);
                    templateTrf = templateTrf.replace("{beneficiary_name}", inquiryTrfResp.getContent().getDestinationAccountName());

                    templateTrf = templateTrf.replace("{amount}", amount_display);
                    templateTrf = templateTrf.replace("{description}", request.getDescription());

                    log.info("Customer Email : " + email);
                    log.info("Transfer Online BSI template was initialized");

                    log.info("Generating content...");
                    template = templateTrf;

                    libFunction.sendEmailAsync(OnlineTrfResp.getContent().getcbsRefCode(), email, "Transaksi Bank Syariah Indonesia (NON BSI)",
                            template, template, landscape);
                }


            } else {
                System.out.println(OnlineTrfResp.getResponseCode() + " <<<========== response code error");
                mbApiResp = MbJsonUtil.createResponseTrf(OnlineTrfResp.getResponseCode(),
                        OnlineTrfResp.getContent().getErrorMessage(),
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

        return mbApiResp;
    }

}
