package com.bsms.service.emoney;

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
import com.bsms.restobjclient.emoney.doInquiryEmoneyReq;
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

@Service("doInquiryEMoney")
public class doInquiryEmoney extends MbBaseServiceImpl implements MbService  {
	@Value("${sql.conf}")
	private String sqlconf;
	
	@Value("${emoney.doInquiry}")
    private String doInquiryEmoney;
	
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
	            String response_msg=null;
	            
				JSONObject value = new JSONObject();
				TrxLimit trxLimit = new TrxLimit();
				int trxType = TrxLimit.EMONEY;
				
		        String response_code = trxLimit.checkLimit(request.getMsisdn(), request.getCustomerLimitType(), 
		        		trxType, Long.parseLong(request.getAmount()), value,sqlconf);
		        
		        System.out.println("RC Check Limit : "+response_code);
		        
		        if ("00".equals(response_code)) 
		        {
		        	System.out.println("::: doInquiry E-Money Microservices Request From Mobile APP :::");
		            System.out.println(new Gson().toJson(request));

					doInquiryEmoneyReq doinquiryemoneyreq = new doInquiryEmoneyReq();

					System.out.println("ID Favorite : " + request.getId_favorit());

					if (request.getId_favorit() == null){
						try {
							
							//construct json
							doinquiryemoneyreq.setCustomer_id(request.getCustomer_id());
							doinquiryemoneyreq.setLanguage(request.getLanguage());
							doinquiryemoneyreq.setCardno(request.getBillkey1());
							doinquiryemoneyreq.setAmount(request.getAmount());
							doinquiryemoneyreq.setAccount_number(request.getAccount_number());
							doinquiryemoneyreq.setAccount_name(request.getAccount_name());


						} catch (Exception e) {
							mbApiResp = MbJsonUtil.createResponseTrf("99",
									"Construct JSON Failed " + e.toString(),
									null,"");
							MbLogUtil.writeLogError(log, "99", e.toString());
						}
					}else {
						
						//========= Read Data From DB ===============//
						try (Connection con = DriverManager.getConnection(sqlconf);) {
							Statement stmt;
							String SQL;

							//============================= check favorite exist or no =================//
							stmt = con.createStatement();
							SQL = "SELECT * from Favorite where id_fav='" + request.getId_favorit() + "'";
							ResultSet rs = stmt.executeQuery(SQL);

							//construct json
							 if (rs.next()) {
								 doinquiryemoneyreq.setCustomer_id(request.getCustomer_id());
									doinquiryemoneyreq.setLanguage(request.getLanguage());
									doinquiryemoneyreq.setCardno(rs.getString("billkey1"));
									doinquiryemoneyreq.setAmount(request.getAmount());
									doinquiryemoneyreq.setAccount_number(request.getAccount_number());
									doinquiryemoneyreq.setAccount_name(request.getAccount_name());
							 }
							 else
							 {
								 doinquiryemoneyreq.setCustomer_id(request.getCustomer_id());
									doinquiryemoneyreq.setLanguage(request.getLanguage());
									doinquiryemoneyreq.setCardno(request.getBillkey1());
									doinquiryemoneyreq.setAmount(request.getAmount());
									doinquiryemoneyreq.setAccount_number(request.getAccount_number());
									doinquiryemoneyreq.setAccount_name(request.getAccount_name());
							 }
							

							con.close();

						}catch (SQLException e) {

							mbApiResp = MbJsonUtil.createResponseTrf("99",
									"Construct JSON Failed " + e.toString(),
									null, "");
							MbLogUtil.writeLogError(log, "99", e.toString());
						}
					}

					//============== Display Request From Mobile API ===================//
					System.out.println("::: doInquiry E-Money Microservices Request From Mobile API:::");
					System.out.println(new Gson().toJson(doinquiryemoneyreq));
					
					//======= Send Data to Microservices =============//
					try {

						HttpEntity<?> req = new HttpEntity(doinquiryemoneyreq, RestUtil.getHeaders());
						RestTemplate restTemps = new RestTemplate();
						String url = doInquiryEmoney;

						ResponseEntity<doInquiryEmoneyResp> response = restTemps.exchange(url, HttpMethod.POST, req, doInquiryEmoneyResp.class);
						doInquiryEmoneyResp doInquiryEmoneyResp = response.getBody();

						System.out.println("::: doInquiry E-Money Microservices Response :::");
						System.out.println(new Gson().toJson(response.getBody()));


						mbApiResp = MbJsonUtil.createResponseTrf(doInquiryEmoneyResp.getResponseCode(),
								doInquiryEmoneyResp.getResponseMessage(),
								doInquiryEmoneyResp.getResponseContent(),
								doInquiryEmoneyResp.getTransactionId());



					} catch (Exception e) {
						mbApiResp = MbJsonUtil.createResponseTrf("99",
								"Send to Microservices Failed " + e.toString(),
								null,"");
						MbLogUtil.writeLogError(log, "99", e.toString());
					}

				}
else if ("01".equals(response_code)) {
		            
		        	if(request.getLanguage().equalsIgnoreCase("en"))    		
		        	{
		        		response_msg = "You cannot do financial transactions.\nPlease come to our branch to activate it.";
		                mbApiResp = MbJsonUtil.createResponseTrf("01",
		                		response_msg,
		        				null,""); 
		        	}
		        	else
		        	{
		        		response_msg = "Anda belum bisa melakukan transaksi finansial.\nSilahkan datang ke cabang untuk mengaktifkannya.";
		                mbApiResp = MbJsonUtil.createResponseTrf("01",
		                		response_msg,
		        				null,""); 
		        	}
		        	
		        	
		            
		        }
		        else if ("02".equals(response_code)) {
		          
		        	if(request.getLanguage().equalsIgnoreCase("en"))    		
		        	{
		        		response_msg = "Your transaction has exceeded the limit.";
		                mbApiResp = MbJsonUtil.createResponseTrf("02",
		                		response_msg,
		        				null,""); 
		        	}
		        	else
		        	{
		        		response_msg = "Transaksi Anda melebihi limit.";
		                mbApiResp = MbJsonUtil.createResponseTrf("02",
		                		response_msg,
		        				null,""); 
		        	}
		        	
		           
		        }
		        else
		        {
		        	
		        	if(request.getLanguage().equalsIgnoreCase("en"))    		
		        	{
		        		mbApiResp = MbJsonUtil.createResponseTrf("03",
								"Limit Check Failed",
			    				null,""); 
		        	}
		        	else
		        	{
		        		mbApiResp = MbJsonUtil.createResponseTrf("03",
								"Check Limit Gagal",
			    				null,""); 
		        	}
		        	
		        }
	        	

	            txLog.setResponse(mbApiResp);
	    		txLogRepository.save(txLog);
	        
	        
	
		
		return  mbApiResp;
	}

}
