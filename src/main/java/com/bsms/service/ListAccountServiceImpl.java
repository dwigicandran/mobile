package com.bsms.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.domain.CardMapping;
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.Security;
import com.bsms.repository.CardmappingRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.SecurityRepository;
import com.bsms.restobj.BalanceInfoResp;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.ListAccountReq;
import com.bsms.restobjclient.ListAccountResp;
import com.bsms.util.RestUtil;
import com.google.gson.Gson;

@Service("listAccount")
public class ListAccountServiceImpl extends MbBaseServiceImpl implements MbService {
	
	@Autowired
	CardmappingRepository cardMappingRepository;
	
	@Autowired
	SecurityRepository securityRepository;
	
	@Autowired
    private MbTxLogRepository txLogRepository;
	
	@Value("${core.service.listaccount}")
    private String accountlistUrl;
	
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
    
    RestTemplate restTemplate = new RestTemplate();
    
    MbApiResp mbApiResp;


	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		
		
		MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
        // get customer from security
        Security security = securityRepository.findByMbSessionId(request.getSessionId());
        Long customerId = security.getCustomerId();
        
		
		return null;
	}

}
