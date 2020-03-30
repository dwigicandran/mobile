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
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.OnlineStatementByTrxDispResp;
import com.bsms.restobjclient.OnlineStatementByTrxReq;
import com.bsms.restobjclient.OnlineStatementByTrxResp;
import com.bsms.restobjclient.OnlineStatementReq;
import com.bsms.restobjclient.OnlineStatementResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("onlineStatementByTrx")
public class MbStatementOnlineByTrxServiceImpl extends MbBaseServiceImpl implements MbService {

	@Value("${core.service.statementonlineByTrx}")
    private String statementOnlineByTrx;
	
	@Value("${core.service.balanceinquiry}")
    private String balanceInqUrl;
	
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

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		
		MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
        OnlineStatementByTrxReq onlineStatementByTrxReq = new OnlineStatementByTrxReq();
        onlineStatementByTrxReq.setCoreUsername(coreUid);
        onlineStatementByTrxReq.setCorePassword(corePass);
        onlineStatementByTrxReq.setCoreCompany(coreCompany);
        onlineStatementByTrxReq.setColomName(coreColumnname);
        onlineStatementByTrxReq.setIdAccount(request.getIdAccount());
        onlineStatementByTrxReq.setOperand(coreOperand);
        onlineStatementByTrxReq.setNumberOfTransaction(request.getNumberOfTransaction());
        
        System.out.println(new Gson().toJson(onlineStatementByTrxReq));
        
        try {
			
        	HttpEntity<?> req = new HttpEntity(onlineStatementByTrxReq, RestUtil.getHeaders());
        	RestTemplate restTemps = new RestTemplate();
        	String url = statementOnlineByTrx;
        	
        	ResponseEntity<OnlineStatementByTrxResp> response = restTemps.exchange(url, HttpMethod.POST, req, OnlineStatementByTrxResp.class);
        	
        	OnlineStatementByTrxResp onlineStatementByTrxResp = response.getBody();
        	System.out.println(new Gson().toJson(response.getBody()));
        	
        	
        	if("00".equals(onlineStatementByTrxResp.getResponseCode())) {
        		
        		OnlineStatementByTrxDispResp onlineStatementByTrxDispResp = new OnlineStatementByTrxDispResp();
        		onlineStatementByTrxDispResp.setContent(onlineStatementByTrxResp.getContent());
        		
        		mbApiResp = MbJsonUtil.createResponse(request, onlineStatementByTrxDispResp,
        				new MbApiStatusResp(onlineStatementByTrxResp.getResponseCode(), MbApiConstant.OK_MESSAGE));
        		
        	} else {
        		
        		String responseCode = onlineStatementByTrxResp.getContent().getErrorCode();
        		String responseDesc = onlineStatementByTrxResp.getContent().getErrorMessage();
        		
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
