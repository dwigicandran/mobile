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

import com.bsms.restobjclient.emoney.doInquiryEmoneyReq;
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
import com.bsms.restobjclient.emoney.ContentEmoneydoPayment;
import com.bsms.restobjclient.emoney.doPaymentEmoneyResp;
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

@Service("doPaymentEMoney")
public class doPaymentEmoney extends MbBaseServiceImpl implements MbService  {
	@Value("${sql.conf}")
    private String sqlconf;

	@Value("${emoney.doPayment}")
    private String doPaymentEMoney;
	
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

				LibFunctionUtil libFunct = new LibFunctionUtil();
				String trx_id = libFunct.getTransactionID(6);

	        	System.out.println("::: doPayment E-Money Microservices Request :::");
	            System.out.println(new Gson().toJson(request));

				doInquiryEmoneyReq doinquiryemoneyreq = new doInquiryEmoneyReq();

				doinquiryemoneyreq.setCorrelationId(trx_id);
				doinquiryemoneyreq.setTransactionId(trx_id);
				doinquiryemoneyreq.setDeliveryChannel("6027");
				doinquiryemoneyreq.setSourceAccountNumber(request.getAccount_number());
				doinquiryemoneyreq.setSourceAccountName(request.getCustomerName());
				doinquiryemoneyreq.setCardNo(request.getBillkey1());
				doinquiryemoneyreq.setAmount(request.getAmount());
				doinquiryemoneyreq.setDescription(request.getDescription());
				doinquiryemoneyreq.setPan(request.getPan());
				doinquiryemoneyreq.setCardAcceptorTerminal("00307180");
				doinquiryemoneyreq.setCardAcceptorMerchantId(request.getMsisdn());
				doinquiryemoneyreq.setCurrency("360");

		System.out.println(new Gson().toJson(doinquiryemoneyreq));
	            
	            try {
	    			
	            	HttpEntity<?> req = new HttpEntity(doinquiryemoneyreq, RestUtil.getHeaders());
	            	RestTemplate restTemps = new RestTemplate();
	            	String url = doPaymentEMoney;
	            	
	    			ResponseEntity<doPaymentEmoneyResp> response = restTemps.exchange(url, HttpMethod.POST, req, doPaymentEmoneyResp.class);
	    			doPaymentEmoneyResp doPaymentEmoneyResp = response.getBody();
	    			
	    			System.out.println("::: doPayment E-Money Microservices Response :::");
	    			System.out.println(new Gson().toJson(response.getBody()));
	    			
	    			
	    			 mbApiResp = MbJsonUtil.createResponseTrf(doPaymentEmoneyResp.getResponseCode(),doPaymentEmoneyResp.getResponseMessage(),doPaymentEmoneyResp.getResponseContent(),doPaymentEmoneyResp.getTransactionId()); 
	    			
	    			 if(doPaymentEmoneyResp.getResponseCode().equalsIgnoreCase("00"))
	    			 {
	    				 JSONObject value = new JSONObject();
	    					TrxLimit trxLimit = new TrxLimit();
	    					int trxType = TrxLimit.EMONEY;
	    					
	    				 trxLimit.LimitUpdate(request.getMsisdn(), request.getCustomerLimitType(), 
	    			        		trxType, Long.parseLong(request.getAmount()), value,sqlconf);
	    				 //favorit
	    			 }
	    			
	    		} catch (Exception e) {
	    			mbApiResp = MbJsonUtil.createResponseTrf("99",
	    					e.toString(),
	        				null,""); 
	    			MbLogUtil.writeLogError(log, "99", e.toString());
	    		}

	            txLog.setResponse(mbApiResp);
	    		txLogRepository.save(txLog);
	        
	        
	
		
		return  mbApiResp;
	}

}
