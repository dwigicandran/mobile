package com.bsms.service;

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

import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.ContentIntTrf;
import com.bsms.restobjclient.InquiryTrfReq;
import com.bsms.restobjclient.InquiryTrfResp;
import com.bsms.restobjclient.OnlineTrfDispResp;
import com.bsms.restobjclient.OnlineTrfResp;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
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
	
	@Value("${core.service.onlineTransfer}")
    private String onlineTransfer;
	
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
    
  
    RestTemplate restTemplate = new RestTemplate();
    
    MbApiResp mbApiResp;
    
    Double amount,fee_admin;
    String amount_display,date_trx,amount_display_admin;

    Client client = ClientBuilder.newClient();
    
    private static Logger log = LoggerFactory.getLogger(MbOnlineTransferServiceImpl.class);
	
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		String BankName="";
		String service_code="";
		String via_atm="";
		 int feature = 0;
		 
		//========== get Bank Data ==============//  
			try (Connection con = DriverManager.getConnection(connectionUrl);) 
	        {
	        	Statement stmt;
	        	String SQL;
	        	
	        	stmt= con.createStatement();
	        	SQL= "SELECT Code, Jenis,Feature, Name FROM Banks with (NOLOCK) INNER JOIN "
	        			+ "BankPrior ON Code = IdBank where Code ='"+request.getDestinationBank()+"'";
	            ResultSet rs = stmt.executeQuery(SQL);
	            
	            while (rs.next()) 
		            {
	            	BankName=rs.getString("Name");
	            	feature=Integer.parseInt(rs.getString("Feature"));
		            }
	            
	            rs.close();
	            stmt.close();
	 	        con.close();
	        
	           
	        } catch (SQLException e) {
	        	
	        	MbLogUtil.writeLogError(log, e, e.toString());
	        	
	        }
			
		
			//========== define service code ==============//  
			if (((feature & FEAT_PRIMA) == FEAT_PRIMA)) {
	            // ATM Prima
	            service_code = "0500";
	            via_atm = "Prima";
	            fee_admin=fee_prima;
	          } else if (((feature & FEAT_BERSAMA) == FEAT_BERSAMA)) {
	            // ATM Bersama
	            service_code = "0200";
	            via_atm = "ATM Bersama";
	            fee_admin=fee_bersama;
	          }
	          else {
	            // ATM Prima by Default
	            service_code = "0500";
	            via_atm = "Prima";
	            fee_admin=fee_prima;
	          }
		 
		LibFunctionUtil libFunct=new LibFunctionUtil();
		String trx_id=libFunct.getTransactionID(6);
		
		MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
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
        
        System.out.println(new Gson().toJson(inquiryTrfReq));
        
        try {
			//=========== Inquiry Trf Online ============//
        	HttpEntity<?> req = new HttpEntity(inquiryTrfReq, RestUtil.getHeaders());
        	RestTemplate restTemps = new RestTemplate();
        	String url = inquiryTransferOnline;
			ResponseEntity<InquiryTrfResp> response = restTemps.exchange(url, HttpMethod.POST, req, InquiryTrfResp.class);
			InquiryTrfResp inquiryTrfResp = response.getBody();
			
			System.out.println("::: Inquiry Online Trf From Back End :::");
			System.out.println(new Gson().toJson(response.getBody()));
			
			//=========== Internal Trf ============//
        	req = new HttpEntity(inquiryTrfReq, RestUtil.getHeaders());
        	restTemps = new RestTemplate();
        	url = onlineTransfer;
        	ResponseEntity<OnlineTrfResp> response_trf = restTemps.exchange(url, HttpMethod.POST, req, OnlineTrfResp.class);
        	OnlineTrfResp OnlineTrfResp = response_trf.getBody();
        	
        	System.out.println("::: Online Trf Process From Back End :::");
			System.out.println(new Gson().toJson(response_trf.getBody()));
        	
        	if("00".equals(OnlineTrfResp.getResponseCode())) {
        		
        		JSONObject value = new JSONObject();
				TrxLimit trxLimit = new TrxLimit();
				int trxType = TrxLimit.TRANSFER_ONLINE;
				
				trxLimit.LimitUpdate(request.getMsisdn(), request.getCustomerLimitType(), 
			        		trxType, Long.parseLong(request.getAmount()), value,connectionUrl);
				
        		amount=Double.parseDouble(request.getAmount());
        		amount_display = libFunct.formatIDRCurrency(amount);
        		amount_display_admin = libFunct.formatIDRCurrency(fee_admin);
        		date_trx = LibFunctionUtil.getDatetime("dd/MM/yyyy HH:mm:ss");
        		
        		List<ContentIntTrf> content = new ArrayList<>();
				content.add(new ContentIntTrf("Status Transaksi","Berhasil",""));
				content.add(new ContentIntTrf("Dari Rekening",request.getAccount_number()+" - Bank Syariah Mandiri",request.getCustomerName()));
				content.add(new ContentIntTrf("Ke Rekening",request.getDestinationAccountNumber()+" - "+BankName,inquiryTrfResp.getContent().getDestinationAccountName()));
				content.add(new ContentIntTrf("Jumlah",amount_display,""));
				content.add(new ContentIntTrf("Biaya Admin",amount_display_admin,""));
				content.add(new ContentIntTrf("Description",request.getDescription(),""));
				content.add(new ContentIntTrf("Referrence Number",request.getRef_no(),""));
				
				OnlineTrfDispResp onlineTrfDispResp = new OnlineTrfDispResp(OnlineTrfResp.getContent().getcbsRefCode(),
						date_trx,
						"Transfer ke Non BSM",
						"Terimakasih telah menggunakan layanan Mandiri Syariah Mobile, semoga layanan kami mendatangkan berkah bagi Anda.",
						content);
				
				mbApiResp = MbJsonUtil.createResponseTrf(OnlineTrfResp.getResponseCode(),
        				"Success",
        				onlineTrfDispResp,trx_id); 
				
				
        		

        		
        	} else {
        		System.out.println(OnlineTrfResp.getResponseCode() + " <<<========== response code error");
        		
        		mbApiResp = MbJsonUtil.createResponseTrf(OnlineTrfResp.getResponseCode(),
        				OnlineTrfResp.getContent().getErrorMessage(),
        				null,""); 
        	}
        	
        	
		} catch (Exception e) {
			mbApiResp = MbJsonUtil.createResponseTrf("99",
					e.toString(),
    				null,""); 
			MbLogUtil.writeLogError(log, "99", e.toString());
		}
        
        txLog.setResponse(mbApiResp);
		txLogRepository.save(txLog);
        
		return mbApiResp;
	}

}
