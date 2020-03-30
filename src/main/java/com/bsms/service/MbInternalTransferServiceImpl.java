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
import com.bsms.restobj.BalanceInfoResp;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.InternalTrfDispResp;
import com.bsms.restobjclient.InternalTrfReq;
import com.bsms.restobjclient.InternalTrfResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.dto.onlinestatement.Content;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("internalTransfer")
public class MbInternalTransferServiceImpl extends MbBaseServiceImpl implements MbService {

	@Value("${core.service.onlineTransfer}")
    private String internalTransfer;
	
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
        
        InternalTrfReq internalTrfReq = new InternalTrfReq();
        internalTrfReq.setCorrelationId(request.getCorrelationId());
        internalTrfReq.setTransactionId(request.getTransactionId());
        internalTrfReq.setDeliveryChannel(request.getDeliveryChannel());
        internalTrfReq.setSourceAccountNumber(request.getSourceAccountNumber());
        internalTrfReq.setSourceAccountName(request.getSourceAccountName());
        internalTrfReq.setDestinationAccountNumber(request.getDestinationAccountNumber());
        internalTrfReq.setDestinationAccountName(request.getDestinationAccountName());
        internalTrfReq.setEncryptedPinBlock(request.getEncryptedPinBlock());
        internalTrfReq.setAmount(request.getAmount());
        internalTrfReq.setDescription(request.getDescription());
        internalTrfReq.setStan(request.getStan());
        internalTrfReq.setPan(request.getPan());
        
        
        System.out.println(new Gson().toJson(internalTrfReq));
        
        try {
			
        	HttpEntity<?> req = new HttpEntity(internalTrfReq, RestUtil.getHeaders());
        	RestTemplate restTemps = new RestTemplate();
        	String url = internalTransfer;
        	
        	ResponseEntity<InternalTrfResp> response = restTemps.exchange(url, HttpMethod.POST, req, InternalTrfResp.class);
        	InternalTrfResp internalTrfResp = response.getBody();
        	
        	InternalTrfDispResp internalTrfDispResp = new InternalTrfDispResp();
        	
        	if("00".equals(internalTrfResp.getResponseCode())) {
        		
        		internalTrfDispResp.setCorrelationId(internalTrfResp.getCorrelationId());
        		internalTrfDispResp.setTransactionId(internalTrfResp.getTransactionId());
        		internalTrfDispResp.setContent(internalTrfResp.getContent());
        		
        		
        		mbApiResp = MbJsonUtil.createResponse(request, internalTrfDispResp,
    					new MbApiStatusResp(internalTrfDispResp.getResponseCode(), MbApiConstant.OK_MESSAGE));
        		
        	} else {
        		System.out.println(internalTrfResp.getResponseCode() + " <<<========== response code error");
        		
        		String responseCode = internalTrfResp.getContent().getErrorCode();
    			String responseDesc = internalTrfResp.getContent().getErrorMessage();
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
