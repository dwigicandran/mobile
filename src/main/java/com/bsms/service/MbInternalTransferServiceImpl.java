package com.bsms.service;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.ContentIntTrf;
import com.bsms.restobjclient.InquiryTrfResp;
import com.bsms.restobjclient.InternalTrfDispResp;
import com.bsms.restobjclient.InternalTrfReq;
import com.bsms.restobjclient.InternalTrfResp;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("internalTransfer")
public class MbInternalTransferServiceImpl extends MbBaseServiceImpl implements MbService {
	@Value("${sql.conf}")
    private String sqlconf;
	
	@Value("${core.service.inquiryTransfer}")
    private String inquiryTransfer;
	
	@Value("${core.service.internalTransfer}")
    private String internalTransfer;
	
	@Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;

    @Autowired
    private MbTxLogRepository txLogRepository;
    
  
    RestTemplate restTemplate = new RestTemplate();
    
    MbApiResp mbApiResp;
    
    Double amount;
    String amount_display,date_trx;

    Client client = ClientBuilder.newClient();
    
    private static Logger log = LoggerFactory.getLogger(MbInternalTransferServiceImpl.class);
	
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		
		LibFunctionUtil libFunct=new LibFunctionUtil();
		String trx_id=libFunct.getTransactionID(6);
		
		MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
        InternalTrfReq internalTrfReq = new InternalTrfReq();
        internalTrfReq.setCorrelationId(trx_id);
        internalTrfReq.setTransactionId(trx_id);
        internalTrfReq.setDeliveryChannel("6027");
        internalTrfReq.setSourceAccountNumber(request.getAccount_number());
        internalTrfReq.setSourceAccountName(request.getCustomerName());
        internalTrfReq.setDestinationAccountNumber(request.getDestinationAccountNumber());
        internalTrfReq.setDestinationAccountName("");
        internalTrfReq.setAmount(request.getAmount());
        internalTrfReq.setDescription(request.getDescription());
        internalTrfReq.setPan(request.getPan());
        internalTrfReq.setCardAcceptorTerminal("00307180");
        internalTrfReq.setCardAcceptorMerchantId(request.getMsisdn());
        internalTrfReq.setCurrency("360");
        
        System.out.println(new Gson().toJson(internalTrfReq));
        
        try {
			//=========== Inquiry Trf ============//
        	HttpEntity<?> req = new HttpEntity(internalTrfReq, RestUtil.getHeaders());
        	RestTemplate restTemps = new RestTemplate();
        	String url = inquiryTransfer;
        	
			ResponseEntity<InquiryTrfResp> response = restTemps.exchange(url, HttpMethod.POST, req, InquiryTrfResp.class);
			InquiryTrfResp inquiryTrfResp = response.getBody();
			
			System.out.println("::: Inquiry Trf From Back End :::");
			System.out.println(new Gson().toJson(response.getBody()));
			
			//=========== Internal Trf ============//
        	req = new HttpEntity(internalTrfReq, RestUtil.getHeaders());
        	restTemps = new RestTemplate();
        	url = internalTransfer;
        	
        	ResponseEntity<InternalTrfResp> response_trf = restTemps.exchange(url, HttpMethod.POST, req, InternalTrfResp.class);
        	InternalTrfResp internalTrfResp = response_trf.getBody();
        	
        	System.out.println("::: Internal Trf From Back End :::");
			System.out.println(new Gson().toJson(response_trf.getBody()));
        	
        	//InternalTrfDispResp internalTrfDispResp = new InternalTrfDispResp();
        	
        	if("00".equals(internalTrfResp.getResponseCode())) {
        		
        		JSONObject value = new JSONObject();
				TrxLimit trxLimit = new TrxLimit();
				int trxType = TrxLimit.TRANSFER;
				
		        trxLimit.LimitUpdate(request.getMsisdn(), request.getCustomerLimitType(), 
		        		trxType, Long.parseLong(request.getAmount()), value,sqlconf);
		        
        		amount=Double.parseDouble(request.getAmount());
        		amount_display = libFunct.formatIDRCurrency(amount);
        		date_trx = LibFunctionUtil.getDatetime("dd/MM/yyyy HH:mm:ss");
        		
        		List<ContentIntTrf> content = new ArrayList<>();
				content.add(new ContentIntTrf("Status Transaksi","Berhasil",""));
				content.add(new ContentIntTrf("Dari Rekening",request.getAccount_number()+" - Bank Syariah Mandiri",request.getCustomerName()));
				content.add(new ContentIntTrf("Ke Rekening",request.getDestinationAccountNumber()+" - Bank Syariah Mandiri",inquiryTrfResp.getContent().getDestinationAccountName()));
				content.add(new ContentIntTrf("Jumlah",amount_display,""));
				content.add(new ContentIntTrf("Description",request.getDescription(),""));
				
				InternalTrfDispResp internalTrfDispResp = new InternalTrfDispResp(internalTrfResp.getContent().getAdditionalData(),
						date_trx,
						"Transfer ke BSM",
						"Terimakasih telah menggunakan layanan Mandiri Syariah Mobile, semoga layanan kami mendatangkan berkah bagi Anda.",
						content);
				
				mbApiResp = MbJsonUtil.createResponseTrf(internalTrfResp.getResponseCode(),
        				"Success",
        				internalTrfDispResp,trx_id); 
				
			
        		

        		
        	} else {
        		System.out.println(internalTrfResp.getResponseCode() + " <<<========== response code error");
        		
        		mbApiResp = MbJsonUtil.createResponseTrf(internalTrfResp.getResponseCode(),
        				internalTrfResp.getContent().getErrorMessage(),
        				null,""); 
        	}
        	
        	
		} catch (Exception e) {
			mbApiResp = MbJsonUtil.createResponseTrf("99",
					e.toString(),
    				null,""); 
			MbLogUtil.writeLogError(log, "99", e.toString());
		}
        
        txLog.setResponse(mbApiResp);
		txLogRepository.save(txLog);
        
		return mbApiResp;
	}

}
