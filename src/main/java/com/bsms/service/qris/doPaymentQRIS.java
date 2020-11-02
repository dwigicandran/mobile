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
import org.springframework.http.client.SimpleClientHttpRequestFactory;
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
import com.bsms.restobjclient.qris.doPaymentQRISResp;
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
import com.bsms.util.RestConfig;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.dto.accountlist.ListOfAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("doPaymentQRIS")
public class doPaymentQRIS extends MbBaseServiceImpl implements MbService  {
	
	@Value("${sql.conf}")
    private String sqlconf;
	
	@Value("${qris.doPayment}")
    private String doPayment;
	
	@Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;
    
    @Autowired
    private MbTxLogRepository txLogRepository;
    
    MbApiResp mbApiResp;
    
    Client client = ClientBuilder.newClient();
	
    private static Logger log = LoggerFactory.getLogger(doPaymentQRIS.class);
    
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
	    		MbApiTxLog txLog = new MbApiTxLog();	
	            txLogRepository.save(txLog);

	        	System.out.println("::: doPayment QRIS Microservices Request :::");
	            System.out.println(new Gson().toJson(request));
	            
	            try {
	    			
	            	HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
	            	
	            	RestTemplate restTemplate = new RestTemplate();
	                ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setConnectTimeout(600000);
	                ((SimpleClientHttpRequestFactory) restTemplate.getRequestFactory()).setReadTimeout(600000);
	            	String url = doPayment;
	            	
	    			ResponseEntity<doPaymentQRISResp> response = restTemplate.exchange(url, HttpMethod.POST, req, doPaymentQRISResp.class);
	    			doPaymentQRISResp doPaymentQRISResp = response.getBody();
	    			
	    			System.out.println("::: doPayment QRIS Microservices Response :::");
	    			System.out.println(new Gson().toJson(response.getBody()));
	    			
	    			 mbApiResp = MbJsonUtil.createResponseTrf(doPaymentQRISResp.getResponseCode(),doPaymentQRISResp.getResponseMessage(),doPaymentQRISResp.getResponseContent(),doPaymentQRISResp.getTransactionId()); 
	    			
	    			 if(doPaymentQRISResp.getResponseCode().equalsIgnoreCase("00"))
	    			 {
	    				 JSONObject value = new JSONObject();
	    					TrxLimit trxLimit = new TrxLimit();
	    					int trxType = TrxLimit.QRIS;
	    				
	    					double d=Double.parseDouble(request.getAmount());
	    					long amount_convert = (new Double(d)).longValue(); 
	    					
	    				 trxLimit.LimitUpdate(request.getMsisdn(), request.getCustomerLimitType(), 
	    			        		trxType,amount_convert, value,sqlconf);
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
