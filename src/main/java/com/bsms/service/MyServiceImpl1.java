package com.bsms.service;

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
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.BalanceInfoResp;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.BalanceInquiryDispResp;
import com.bsms.restobjclient.BalanceInquiryReq;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;
import com.dto.balanceinfo.GIDIEACCTBALTWSDetailType;
import com.dto.balanceinfo.MIDIEACCTBALTWSDetailType;

@Service("service1")
public class MyServiceImpl1 extends MbBaseServiceImpl implements MbService {

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
    
    RestTemplate restTemplate = new RestTemplate();
    
    MbApiResp mbApiResp;
    
    String accountNumber = "";
    String amount = "";

    Client client = ClientBuilder.newClient();
	
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
        
        /*MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
        BalanceInquiryReq balanceInqReq = new BalanceInquiryReq();
        balanceInqReq.setCoreUsername(coreUid);
        balanceInqReq.setCorePassword(corePass);
        balanceInqReq.setCoreCompany(coreCompany);
        balanceInqReq.setColomName(coreColumnname);
        balanceInqReq.setIdAccount(request.getIdAccount());
        balanceInqReq.setOperand(coreOperand);
        
        BalanceInquiryDispResp balanceInquiryDispResp = new BalanceInquiryDispResp();
        
        System.out.println(new Gson().toJson(balanceInqReq));
		
        try {
        	
        	HttpEntity<?> req = new HttpEntity(balanceInqReq, RestUtil.getHeaders());
        	
        	RestTemplate restTemps = new RestTemplate();

        	String url = balanceInqUrl;
        	
        	ResponseEntity<BalanceInfoResp> response = restTemps.exchange(url, HttpMethod.POST, req, BalanceInfoResp.class);
        	
        	BalanceInfoResp balanceInfoResp = response.getBody();
        	
        	if("00".equals(balanceInfoResp.getResponseCode())) {
        		
        		for(GIDIEACCTBALTWSDetailType gIDIEACCTBALTWSDetailType : balanceInfoResp.getContent().getgIDIEACCTBALTWSDetailType()) {
            		for(MIDIEACCTBALTWSDetailType mIDIEACCTBALTWSDetailType : gIDIEACCTBALTWSDetailType.getMidieacctbaltwsDetailType()) {
            			
            			if (mIDIEACCTBALTWSDetailType.getAccountNumber() != null
    							&& !mIDIEACCTBALTWSDetailType.getAccountNumber().trim().equals("")) {
    						accountNumber = mIDIEACCTBALTWSDetailType.getAccountNumber();
    					}
            			
            			if (mIDIEACCTBALTWSDetailType.getAvailableBalance() != null
    							&& !mIDIEACCTBALTWSDetailType.getAvailableBalance().trim().equals("")) {
            				amount = mIDIEACCTBALTWSDetailType.getAvailableBalance();
    					}
            		}
            	}
            	
            	System.out.println(new Gson().toJson(response.getBody()));
            	
            	balanceInquiryDispResp.setCorrelationId(balanceInfoResp.getCorrelationId());
            	balanceInquiryDispResp.setTransactionId(TrxIdUtil.getTransactionID(1));
            	balanceInquiryDispResp.setResponse("Saldo anda saat ini pada akun : " + accountNumber + ", "
            			+ "sebesar Rp." + amount + " \\n\\nVersi baru Mandiri Syariah Mobile Banking sudah tersedia.\\n\\nSilahkan update aplikasi Anda untuk kenyamanan dan kemudahan transaksi Anda.");
            	balanceInquiryDispResp.setShare("Transaksi Informasi saldo  Informasi Saldo menggunakan aplikasi BSM mobile banking berhasil, download via Google Play Store");
            	
            	mbApiResp = MbJsonUtil.createResponse(request, balanceInquiryDispResp,
    					new MbApiStatusResp(balanceInfoResp.getResponseCode(), MbApiConstant.OK_MESSAGE));
        		
        	} else {
        		
        		String responseCode = balanceInfoResp.getContent().getErrorCode();
    			String responseDesc = balanceInfoResp.getContent().getErrorMessage();
    			mbApiResp = MbJsonUtil.createResponse(request, new MbApiStatusResp(responseCode, responseDesc));
        	}
        	
		} catch (Exception e) {
			mbApiResp = MbJsonUtil.createExceptionSL(request, e);
		}
		
		txLog.setResponse(mbApiResp);
		txLogRepository.save(txLog);*/
		
		mbApiResp = MbJsonUtil.createResponse(request, new MbApiStatusResp("00", "Successful"));
		
		return mbApiResp;
	}

}
