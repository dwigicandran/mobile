package com.bsms.service.emoney;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import com.bsms.restobjclient.emoney.doInquiryEmoneyReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.emoney.doUpdateEmoneyResp;
import com.bsms.restobjclient.transfer.InquiryTrfResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.service.transfer.MbInquiryOnlineTrfService;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.google.gson.Gson;

@Service("doUpdateEmoney")
public class doUpdateEmoney extends MbBaseServiceImpl implements MbService  {

	@Value("${emoney.doUpdate}")
    private String doUpdateEmoney;
    
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

	        	System.out.println("::: doUpdateEmoney E-Money Microservices Request :::");
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
	            	String url = doUpdateEmoney;
	            	
	    			ResponseEntity<doUpdateEmoneyResp> response = restTemps.exchange(url, HttpMethod.POST, req, doUpdateEmoneyResp.class);
	    			doUpdateEmoneyResp doUpdateEmoneyResp = response.getBody();
	    			
	    			System.out.println("::: doUpdateEmoney E-Money Microservices Response :::");
	    			System.out.println(new Gson().toJson(response.getBody()));
	    			
	    			
	    			 mbApiResp = MbJsonUtil.createResponseTrf(doUpdateEmoneyResp.getResponseCode(),doUpdateEmoneyResp.getResponseMessage(),doUpdateEmoneyResp.getResponseContent(),doUpdateEmoneyResp.getTransactionId()); 
	    			
	    			
	    			
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
