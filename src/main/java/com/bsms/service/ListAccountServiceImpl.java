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

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.CardMapping;
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbAppContent;
import com.bsms.domain.Security;
import com.bsms.repository.CardmappingRepository;
import com.bsms.repository.CustomerRepository;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.SecurityRepository;
import com.bsms.restobj.BalanceInfoResp;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.ListAccountDispResp;
import com.bsms.restobjclient.ListAccountReq;
import com.bsms.restobjclient.ListAccountResp;
import com.bsms.restobjclient.OnlineStatementResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;
import com.google.gson.Gson;

@Service("listAccount")
public class ListAccountServiceImpl extends MbBaseServiceImpl implements MbService {
	
	@Autowired
	CardmappingRepository cardMappingRepository;
	
	@Autowired
	SecurityRepository securityRepository;
	
	@Autowired
	CardmappingRepository cardmappingRepository;
	
	@Autowired
	MbAppContentRepository mbAppContentRepository;
	
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
    
    private String responseDesc;
	private String responseCode;
    
    MbApiResp mbApiResp;

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		
		MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
        // get customer from security
        Security security = securityRepository.findByMbSessionId(request.getSessionId());
        Long customerId = security.getCustomerId();
        
        List<String> abc = new ArrayList<String>();
        //abc.add("7095539976");
        //abc.add("7066507445");
        
        List<CardMapping> cardMapping = cardmappingRepository.findAccountnumberByCustomerid(customerId);
        
        for(CardMapping cm : cardMapping) {
        	abc.add(cm.getAccountnumber());
        }
        
        ListAccountReq listAccountReq = new ListAccountReq();
        
        listAccountReq.setTransactionId(TrxIdUtil.getTransactionID(6));
        listAccountReq.setCoreUsername(coreUid);
        listAccountReq.setCorePassword(corePass);
        listAccountReq.setCoreCompany(coreCompany);
        listAccountReq.setColomName(coreColumnname);
        listAccountReq.setOperand(coreOperand);
        listAccountReq.setListAccountNumber(abc);
        
        System.out.println(new Gson().toJson(listAccountReq));
        
        try {
			
        	HttpEntity<?> req = new HttpEntity(listAccountReq, RestUtil.getHeaders());
        	
        	RestTemplate restTemps = new RestTemplate();

        	String url = accountlistUrl;
        	ResponseEntity<ListAccountResp> response = restTemps.exchange(url, HttpMethod.POST, req, ListAccountResp.class);
        	ListAccountResp listAccountResp = response.getBody();
        	System.out.println(new Gson().toJson(response.getBody()));
        	
        	if("00".equals(listAccountResp.getResponseCode())) {
        		
        		ListAccountDispResp listAccountDispResp = new ListAccountDispResp();
        		listAccountDispResp.setBcust(listAccountResp.getContent().getBcust());
        		listAccountDispResp.setCustdisp(listAccountResp.getContent().getCustdisp());
        		listAccountDispResp.setListOfAccount(listAccountResp.getContent().getListOfAccount());
        		
        		mbApiResp = MbJsonUtil.createResponse(request, listAccountDispResp,
    					new MbApiStatusResp(listAccountDispResp.getResponseCode(), MbApiConstant.OK_MESSAGE), listAccountResp.getResponseCode(), MbApiConstant.SUCCESS_MSG);
        		
        	} else {
        		MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
    			responseDesc = mbAppContent.getDescription();
    			responseCode = MbApiConstant.ERR_CODE;
    			mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
        	}
        	
		} catch (Exception e) {
			MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
			responseDesc = mbAppContent.getDescription();
			responseCode = MbApiConstant.ERR_CODE;
			mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
		}
        
		 return mbApiResp;
	}

}
