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
import com.bsms.restobjclient.transfer.InquiryTrfResp;
import com.bsms.restobjclient.transfer.InternalTrfDispResp;
import com.bsms.restobjclient.transfer.InternalTrfReq;
import com.bsms.restobjclient.transfer.InternalTrfResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("internalTransfer")
public class MbInternalTransferServiceImpl extends MbBaseServiceImpl implements MbService {
    @Value("${sql.conf}")
    private String sqlconf;

    @Value("${core.service.inquiryTransfer}")
    private String inquiryTransfer;

    @Value("${core.service.internalTransfer}")
    private String internalTransfer;

    @Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;
    
    @Value("${template.trf}") 
	private String templateTRF;
	
	@Value("${template.logo}") 
	private String templateLogo;
	
	@Value("${tmp.folder}") 
	private static String tmp_folder;


    RestTemplate restTemplate = new RestTemplate();

    MbApiResp mbApiResp;

    Double amount;
    String amount_display, date_trx;

    Client client = ClientBuilder.newClient();

    String DestinationAccountNumber = null;

    private static Logger log = LoggerFactory.getLogger(MbInternalTransferServiceImpl.class);

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {

        LibFunctionUtil libFunct = new LibFunctionUtil();
        String trx_id = libFunct.getTransactionID(6);

        MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);

        System.out.println("ID Favorit : " + request.getId_favorit());

        InternalTrfReq internalTrfReq = new InternalTrfReq();

        if (request.getId_favorit() == null) {
            internalTrfReq.setCorrelationId(trx_id);
            internalTrfReq.setTransactionId(trx_id);
            internalTrfReq.setDeliveryChannel("6027");
            internalTrfReq.setSourceAccountNumber(request.getAccount_number());
            internalTrfReq.setSourceAccountName(request.getCustomerName());
            internalTrfReq.setDestinationAccountNumber(request.getDestinationAccountNumber());
            internalTrfReq.setDestinationAccountName("");
            internalTrfReq.setAmount(request.getAmount());
            internalTrfReq.setDescription(request.getDescription());
            internalTrfReq.setPan(request.getPan());
            internalTrfReq.setCardAcceptorTerminal("00307180");
            internalTrfReq.setCardAcceptorMerchantId(request.getMsisdn());
            internalTrfReq.setCurrency("360");
            internalTrfReq.setLanguage(request.getLanguage());//adition by Dwi


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
                    internalTrfReq.setCorrelationId(trx_id);
                    internalTrfReq.setTransactionId(trx_id);
                    internalTrfReq.setDeliveryChannel("6027");
                    internalTrfReq.setSourceAccountNumber(request.getAccount_number());
                    internalTrfReq.setSourceAccountName(request.getCustomerName());
                    internalTrfReq.setDestinationAccountNumber(rs.getString("destinationAccountNumber"));
                    internalTrfReq.setDestinationAccountName("");
                    internalTrfReq.setAmount(request.getAmount());
                    internalTrfReq.setDescription(request.getDescription());
                    internalTrfReq.setPan(request.getPan());
                    internalTrfReq.setCardAcceptorTerminal("00307180");
                    internalTrfReq.setCardAcceptorMerchantId(request.getMsisdn());
                    internalTrfReq.setCurrency("360");
                    internalTrfReq.setLanguage(request.getLanguage());//adition by Dwi

                    DestinationAccountNumber = rs.getString("destinationAccountNumber");
                } else {

                    internalTrfReq.setCorrelationId(trx_id);
                    internalTrfReq.setTransactionId(trx_id);
                    internalTrfReq.setDeliveryChannel("6027");
                    internalTrfReq.setSourceAccountNumber(request.getAccount_number());
                    internalTrfReq.setSourceAccountName(request.getCustomerName());
                    internalTrfReq.setDestinationAccountNumber(request.getDestinationAccountNumber());
                    internalTrfReq.setDestinationAccountName("");
                    internalTrfReq.setAmount(request.getAmount());
                    internalTrfReq.setDescription(request.getDescription());
                    internalTrfReq.setPan(request.getPan());
                    internalTrfReq.setCardAcceptorTerminal("00307180");
                    internalTrfReq.setCardAcceptorMerchantId(request.getMsisdn());
                    internalTrfReq.setCurrency("360");
                    internalTrfReq.setLanguage(request.getLanguage());//adition by Dwi

                    DestinationAccountNumber = request.getDestinationAccountNumber();
                }

                con.close();


            } catch (SQLException e) {

                MbLogUtil.writeLogError(log, e, e.toString());

                mbApiResp = MbJsonUtil.createResponseTrf("99",
                        "Internal Transfer Failed",
                        null, "");

            }
        }


        System.out.println(new Gson().toJson(internalTrfReq));

        try {
            //=========== Inquiry Trf ============//
            HttpEntity<?> req = new HttpEntity(internalTrfReq, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            String url = inquiryTransfer;

            ResponseEntity<InquiryTrfResp> response = restTemps.exchange(url, HttpMethod.POST, req, InquiryTrfResp.class);
            InquiryTrfResp inquiryTrfResp = response.getBody();

            System.out.println("::: Inquiry Trf From Back End :::");
            System.out.println(new Gson().toJson(response.getBody()));

            //=========== Internal Trf ============//
            req = new HttpEntity(internalTrfReq, RestUtil.getHeaders());
            restTemps = new RestTemplate();
            url = internalTransfer;

            ResponseEntity<InternalTrfResp> response_trf = restTemps.exchange(url, HttpMethod.POST, req, InternalTrfResp.class);
            InternalTrfResp internalTrfResp = response_trf.getBody();

            System.out.println("::: Internal Trf From Back End :::");
            System.out.println(new Gson().toJson(response_trf.getBody()));

            //InternalTrfDispResp internalTrfDispResp = new InternalTrfDispResp();

            if ("00".equals(internalTrfResp.getResponseCode())) {

                JSONObject value = new JSONObject();
                TrxLimit trxLimit = new TrxLimit();
                int trxType = TrxLimit.TRANSFER;
                updLimitTrx(request.getMsisdn(), request.getCustomerLimitType(),
                        trxType, Long.parseLong(request.getAmount()));

                amount = Double.parseDouble(request.getAmount());
                amount_display = libFunct.formatIDRCurrency(amount);
                date_trx = LibFunctionUtil.getDatetime("dd/MM/yyyy HH:mm:ss");

                String title = "Transfer Ke BSI";
                List<ContentIntTrf> content = new ArrayList<>();

                if (internalTrfReq.getLanguage().equals("id")) {
                    content.add(new ContentIntTrf("Status Transaksi", "Berhasil", ""));
                    content.add(new ContentIntTrf("Dari Rekening", TextUtil.maskString(request.getAccount_number(), 0, 6, 'X') + " - Bank Syariah Indonesia", request.getCustomerName()));
                    content.add(new ContentIntTrf("Ke Rekening", DestinationAccountNumber + " - Bank Syariah Indonesia", inquiryTrfResp.getContent().getDestinationAccountName()));
                    content.add(new ContentIntTrf("Jumlah", amount_display, ""));
                    content.add(new ContentIntTrf("Keterangan", request.getDescription(), ""));
                } else { //penambahan resi bahasa inggris oleh Dwi
                    content.add(new ContentIntTrf("Transaction Status", "Successfull", ""));
                    content.add(new ContentIntTrf("From Account", TextUtil.maskString(request.getAccount_number(), 0, 6, 'X') + " - Bank Syariah Indonesia", request.getCustomerName()));
                    content.add(new ContentIntTrf("To Account", DestinationAccountNumber + " - Bank Syariah Indonesia", inquiryTrfResp.getContent().getDestinationAccountName()));
                    content.add(new ContentIntTrf("Total", amount_display, ""));
                    content.add(new ContentIntTrf("Description", request.getDescription(), ""));
                    title = "Transfer to BSI";
                }

                InternalTrfDispResp internalTrfDispResp = new InternalTrfDispResp(internalTrfResp.getContent().getAdditionalData(),
                        date_trx,
                        title,
                        "Terimakasih telah menggunakan layanan BSI Mobile, semoga layanan kami mendatangkan berkah bagi Anda.",
                        content);

                mbApiResp = MbJsonUtil.createResponseTrf(internalTrfResp.getResponseCode(),
                        "Success",
                        internalTrfDispResp, trx_id);
                
                LibFunctionUtil libFunction=new LibFunctionUtil();
	            
	            boolean landscape = false;
	            String template = null;
	            String templateTrf=null;
	            String email;
	            
	            email=request.getCustomerEmail();
	            
	            BufferedInputStream bis = new BufferedInputStream(new ClassPathResource(templateTRF).getInputStream());
	        	byte[] buffer = new byte [bis.available()];
	        	bis.read(buffer, 0, buffer.length);
	        	bis.close();
	        	
	        	templateTrf = new String(buffer);
	        	templateTrf = templateTrf.replace("{image}", new ClassPathResource(templateLogo).getURL().toString());
	        	
	        	templateTrf = templateTrf.replace("{status}", "BERHASIL");
	        	
	        	templateTrf = templateTrf.replace("{trans_ref}", internalTrfResp.getContent().getAdditionalData());
	        	templateTrf = templateTrf.replace("{struck}", internalTrfResp.getCorrelationId());
	        	templateTrf = templateTrf.replace("{terminal}", TextUtil.maskString(request.getMsisdn(), 0, 8, 'X'));
	        	templateTrf = templateTrf.replace("{date_time}", date_trx);
	        	
	        	templateTrf = templateTrf.replace("{name}", request.getCustomerName());
	        	templateTrf = templateTrf.replace("{account}", TextUtil.maskString(request.getAccount_number(), 0, 6, 'X'));
	        	
	        	templateTrf = templateTrf.replace("{payment_id}", DestinationAccountNumber);
	        	templateTrf = templateTrf.replace("{code_name}", "Bank Syariah Indonesia");
	        	templateTrf = templateTrf.replace("{beneficiary_name}", inquiryTrfResp.getContent().getDestinationAccountName());
	        	
	        	templateTrf = templateTrf.replace("{amount}", amount_display);
	        	templateTrf = templateTrf.replace("{description}", request.getDescription());
	        	
	        	log.info("Transfer Internal BSI template was initialized");
	        	
	        	log.info("Generating content...");
	        	template=templateTrf;
	        	
	            libFunction.sendEmailAsync(internalTrfResp.getContent().getAdditionalData(), email, "Transaksi Bank Syariah Indonesia",
	                      template, template, landscape);


            } else {
                System.out.println(internalTrfResp.getResponseCode() + " <<<========== response code error");
                mbApiResp = MbJsonUtil.createResponseTrf(internalTrfResp.getResponseCode(),
                        internalTrfResp.getContent().getErrorMessage(),
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
