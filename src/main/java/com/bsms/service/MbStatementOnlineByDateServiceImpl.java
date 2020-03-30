package com.bsms.service;

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
import com.bsms.restobjclient.BalanceInquiryDispResp;
import com.bsms.restobjclient.OnlineStatementDispResp;
import com.bsms.restobjclient.OnlineStatementReq;
import com.bsms.restobjclient.OnlineStatementResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("onlineStatementByDate")
public class MbStatementOnlineByDateServiceImpl extends MbBaseServiceImpl implements MbService {

	@Value("${core.service.statementonline}")
    private String statementOnline;
	
	@Value("${core.uid}")
	private String coreUid;
	
	@Value("${core.pass}")
	private String corePass;
	
	@Value("${core.company}")
	private String coreCompany;
	
	@Value("${core.columnname}")
	private String coreColumnname;
	
	@Value("${core.operand}")
	private String coreOperand;
	
	@Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;
    
    MbApiResp mbApiResp;
    
    RestTemplate restTemplate = new RestTemplate();
	
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
    	
    	MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
        OnlineStatementReq onlineStatementReq = new OnlineStatementReq();
        
        onlineStatementReq.setCorrelationId("11111");
        onlineStatementReq.setTransactionId("22222");
        onlineStatementReq.setCoreUsername("DEVTWS");
        onlineStatementReq.setCorePassword("123123");
        onlineStatementReq.setCoreCompany("ID0010001");
        onlineStatementReq.setAccountNumber(request.getAccount_number());
        onlineStatementReq.setStartDate(request.getStartDate()); // 20160101
        onlineStatementReq.setEndDate(request.getEndDate()); // 20160821
        
        System.out.println(new Gson().toJson(onlineStatementReq));
    	
    	try {
			
    		HttpEntity<?> req = new HttpEntity(onlineStatementReq, RestUtil.getHeaders());
        	
        	RestTemplate restTemps = new RestTemplate();

        	String url = statementOnline;
        	ResponseEntity<OnlineStatementResp> response = restTemps.exchange(url, HttpMethod.POST, req, OnlineStatementResp.class);
    		
        	OnlineStatementResp onlineStatementResp = response.getBody();
        	System.out.println(new Gson().toJson(response.getBody()));
        	
        	if("00".equals(onlineStatementResp.getResponseCode())) {
        	
        		OnlineStatementDispResp onlineStatementDispResp = new OnlineStatementDispResp();
            	onlineStatementDispResp.setContent(onlineStatementResp.getContent());
            	
            	mbApiResp = MbJsonUtil.createResponse(request, onlineStatementDispResp,
    					new MbApiStatusResp(onlineStatementResp.getResponseCode(), MbApiConstant.OK_MESSAGE));
        		
        	} else {
        		
        		String responseCode = onlineStatementResp.getContent().getErrorCode();
        		String responseDesc = onlineStatementResp.getContent().getErrorMessage();
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
