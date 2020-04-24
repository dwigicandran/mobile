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

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.Bank;
import com.bsms.restobjclient.BankDispResp;
import com.bsms.restobjclient.ContentInqTrf;
import com.bsms.restobjclient.InquiryTrfDispResp;
import com.bsms.restobjclient.InquiryTrfReq;
import com.bsms.restobjclient.InquiryTrfResp;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("inquiryOnlineTransfer")
public class MbInquiryOnlineTrfService extends MbBaseServiceImpl implements MbService  {
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
		
		String BankName="";
		String service_code="";
		String via_atm="";
		 int feature = 0;
		 String response_msg=null;
		
		//=============== cek limit ================//
			JSONObject value = new JSONObject();
			TrxLimit trxLimit = new TrxLimit();
			int trxType = TrxLimit.TRANSFER_ONLINE;
			
	        String response_code = trxLimit.checkLimit(request.getMsisdn(), request.getCustomerLimitType(), 
	        		trxType, Long.parseLong(request.getAmount()), value,sqlconf);
	        
	        System.out.println("RC Check Limit : "+response_code);
	        
	        if ("00".equals(response_code)) {
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
	              } else if (((feature & FEAT_BERSAMA) == FEAT_BERSAMA)) {
	                // ATM Bersama
	                service_code = "0200";
	                via_atm = "ATM Bersama";
	              }
	              else {
	                // ATM Prima by Default
	                service_code = "0500";
	                via_atm = "Prima";
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
	    			
	    			if("00".equals(inquiryTrfResp.getResponseCode())) {
	    				
	    				String trf_method=request.getTrf_method();
	    				if(trf_method.equalsIgnoreCase("1"))
	    				{
	    					trf_method="Online";
	    				}
	    				else
	    				{
	    					trf_method="SKN";
	    				}
	    				
	    				List<ContentInqTrf> content = new ArrayList<>();
	    				content.add(new ContentInqTrf("Bank Destination",BankName));
	    				content.add(new ContentInqTrf("Transfer Method",trf_method));
	    				content.add(new ContentInqTrf("Amount",request.getAmount()));
	    				content.add(new ContentInqTrf("Description",request.getDescription()));
	    				content.add(new ContentInqTrf("Referrence Number",request.getRef_no()));
	    				
	    				InquiryTrfDispResp inquiryTrfDispResp = new InquiryTrfDispResp(request.getAccount_number(),
	    						request.getCustomerName(),
	    						request.getDestinationAccountNumber(),
	    						inquiryTrfResp.getContent().getDestinationAccountName(),
	    								content,trx_id);
	    				
	    				
	            		mbApiResp = MbJsonUtil.createResponseTrf(inquiryTrfResp.getResponseCode(),
	            				"Success",
	            				inquiryTrfDispResp,trx_id); 
	    				 
	    				
	    			} else {
	    				System.out.println(inquiryTrfResp.getResponseCode() + " <<<========== response code error");
	            	
	        			mbApiResp = MbJsonUtil.createResponseTrf(inquiryTrfResp.getResponseCode(),
	        					inquiryTrfResp.getContent().getErrorMessage(),
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
	        }
	        else if ("01".equals(response_code)) {
	            
	        	response_msg = "Anda belum bisa melakukan transaksi finansial.\nSilahkan datang ke cabang untuk mengaktifkannya.";
	                mbApiResp = MbJsonUtil.createResponseTrf("01",
	                		response_msg,
	        				null,""); 
	            
	        }
	        else if ("02".equals(response_code)) {
	          
	        	response_msg = "Transaksi Anda melebihi limit.";
	                mbApiResp = MbJsonUtil.createResponseTrf("02",
	                		response_msg,
	        				null,""); 
	           
	        }
	        else
	        {
	        	mbApiResp = MbJsonUtil.createResponseTrf("03",
						"Check Limit Gagal",
	    				null,""); 
	        }
	
		
		return mbApiResp;
	}

}
