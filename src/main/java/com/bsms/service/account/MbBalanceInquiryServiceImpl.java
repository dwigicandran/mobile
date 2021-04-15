package com.bsms.service.account;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbAppContent;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.account.AccountInfo;
import com.bsms.restobjclient.account.BalanceInfoResp;
import com.bsms.restobjclient.account.BalanceInquiryDispResp;
import com.bsms.restobjclient.account.BalanceInquiryReq;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;
import com.dto.balanceinfo.Content;
import com.dto.balanceinfo.GIDIEACCTBALTWSDetailType;
import com.dto.balanceinfo.MIDIEACCTBALTWSDetailType;

@Service("balanceInquiry")
public class MbBalanceInquiryServiceImpl extends MbBaseServiceImpl implements MbService {

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
	private MbAppContentRepository mbAppContentRepository;
	
    @Autowired
    private MbTxLogRepository txLogRepository;
    
    RestTemplate restTemplate = new RestTemplate();
    
    MbApiResp mbApiResp;
    
    String accountNumber = "";
    String amount = "";
    String currency = "";
    private String responseDesc;
	private String responseCode;

    Client client = ClientBuilder.newClient();
	
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
        
        MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
        BalanceInquiryReq balanceInqReq = new BalanceInquiryReq();
        balanceInqReq.setCoreUsername(coreUid);
        balanceInqReq.setCorePassword(corePass);
        balanceInqReq.setCoreCompany(coreCompany);
        balanceInqReq.setColomName(coreColumnname);
        balanceInqReq.setIdAccount(request.getIdAccount());
        balanceInqReq.setOperand(coreOperand);
        balanceInqReq.setAccountNumber(request.getAccount_number());
        
        BalanceInquiryDispResp balanceInquiryDispResp = new BalanceInquiryDispResp();
        
        System.out.println(new Gson().toJson(balanceInqReq));
		
        try {
        	
        	HttpEntity<?> req = new HttpEntity(balanceInqReq, RestUtil.getHeaders());
        	RestTemplate restTemps = new RestTemplate();
        	String url = balanceInqUrl;
        	
        	ResponseEntity<BalanceInfoResp> response = restTemps.exchange(url, HttpMethod.POST, req, BalanceInfoResp.class);
        	BalanceInfoResp balanceInfoResp = response.getBody();
        	
        	System.out.println(new Gson().toJson(balanceInfoResp));
        	
        	if("00".equals(balanceInfoResp.getResponseCode())) {
        		
        		amount = balanceInfoResp.getContent().getAccountInfo().getAvailableBalance();
        		accountNumber = balanceInfoResp.getContent().getAccountInfo().getAccountNumber();
        		currency = balanceInfoResp.getContent().getAccountInfo().getCurrency();
        		
        		if("IDR".equals(currency)) {
        			currency = "Rp";
        		}
        		
        		balanceInquiryDispResp.setCorrelationId(balanceInfoResp.getCorrelationId());
            	balanceInquiryDispResp.setTransactionId(TrxIdUtil.getTransactionID(6));
            	
//            	MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("400001", "id");
//            	String pesan = mbAppContent.getDescription();
//            	String pesan2 = pesan.replace("[account]", accountNumber);
//            	String pesan3 = pesan2.replace("[amount]", amount);
//            	String pesanFulll = pesan3.replace("[CR]", "\n");
            	
            	balanceInquiryDispResp.setAccountNumber(accountNumber);
            	balanceInquiryDispResp.setAmount(amount);
            	balanceInquiryDispResp.setCurrency(currency);
            	
            	mbApiResp = MbJsonUtil.createResponse(request, balanceInquiryDispResp,
    					new MbApiStatusResp(balanceInfoResp.getResponseCode(), MbApiConstant.OK_MESSAGE), balanceInfoResp.getResponseCode(), MbApiConstant.SUCCESS_MSG_INQ_BAL);
        		
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
		
		txLog.setResponse(mbApiResp);
		txLogRepository.save(txLog);
		
		return mbApiResp;
	}

}
