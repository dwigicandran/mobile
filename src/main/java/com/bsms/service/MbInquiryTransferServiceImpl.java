package com.bsms.service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

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
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.InquiryTrfDispResp;
import com.bsms.restobjclient.InquiryTrfReq;
import com.bsms.restobjclient.InquiryTrfResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

//addition by Dodo
import com.bsms.util.TrxIdUtil;

@Service("inquiryTransfer")
public class MbInquiryTransferServiceImpl extends MbBaseServiceImpl implements MbService  {

	@Value("${core.service.inquiryTransfer}")
    private String inquiryTransfer;
	
	@Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;
    
    RestTemplate restTemplate = new RestTemplate();
    
    MbApiResp mbApiResp;
    
    String accountNumber = "";
    String amount = "";

    Client client = ClientBuilder.newClient();
	
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		
		MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
        InquiryTrfReq inquiryTrfReq = new InquiryTrfReq();
        
        inquiryTrfReq.setCorrelationId(TrxIdUtil.getTransactionID(6));//addition by Dodo
		inquiryTrfReq.setTransactionId(TrxIdUtil.getTransactionID(6));//addition by Dodo
        inquiryTrfReq.setDeliveryChannel("6027");//addition by Dodo
        inquiryTrfReq.setSourceAccountNumber(request.getAccount_number());//addition by Dodo
        inquiryTrfReq.setSourceAccountName(request.getSourceAccountName());
        inquiryTrfReq.setDestinationAccountNumber(request.getDestinationAccountNumber());
        inquiryTrfReq.setDestinationAccountName(request.getDestinationAccountName());
        //inquiryTrfReq.setEncryptedPinBlock(request.getEncryptedPinBlock());
        inquiryTrfReq.setAmount(request.getAmount());
        inquiryTrfReq.setDescription(request.getDescription());
        //inquiryTrfReq.setStan(request.getStan());
        inquiryTrfReq.setPan(request.getPan());
        inquiryTrfReq.setCardAcceptorTerminal("00307180");//addition by Dodo
        inquiryTrfReq.setCardAcceptorMerchantId(request.getMsisdn());//addition by Dodo
        inquiryTrfReq.setCurrency("360");//addition by Dodo

        System.out.println(new Gson().toJson(inquiryTrfReq));
        
        try {
			
        	HttpEntity<?> req = new HttpEntity(inquiryTrfReq, RestUtil.getHeaders());
        	RestTemplate restTemps = new RestTemplate();
        	String url = inquiryTransfer;
        	
			ResponseEntity<InquiryTrfResp> response = restTemps.exchange(url, HttpMethod.POST, req, InquiryTrfResp.class);
			InquiryTrfResp inquiryTrfResp = response.getBody();
			
			InquiryTrfDispResp inquiryTrfDispResp = new InquiryTrfDispResp();
			
			if("00".equals(inquiryTrfResp.getResponseCode())) {
				
				inquiryTrfDispResp.setCorrelationId(inquiryTrfResp.getCorrelationId());
        		inquiryTrfDispResp.setTransactionId(inquiryTrfResp.getTransactionId());
        		inquiryTrfDispResp.setContent(inquiryTrfResp.getContent());
        		
        		mbApiResp = MbJsonUtil.createResponse(request, inquiryTrfDispResp,
    					new MbApiStatusResp(inquiryTrfResp.getResponseCode(), MbApiConstant.OK_MESSAGE)); 
				 
				
			} else {
				System.out.println(inquiryTrfResp.getResponseCode() + " <<<========== response code error");
        		String responseCode = inquiryTrfResp.getContent().getErrorCode();
    			String responseDesc = inquiryTrfResp.getContent().getErrorMessage();
    			mbApiResp = MbJsonUtil.createResponse(request, new MbApiStatusResp(responseCode, responseDesc));
				
			}
		} catch (Exception e) {
			mbApiResp = MbJsonUtil.createExceptionSL(request, e);
		}

        txLog.setResponse(mbApiResp);
		txLogRepository.save(txLog);
		
		return mbApiResp;
	}

}
