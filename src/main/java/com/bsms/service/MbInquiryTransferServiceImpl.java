package com.bsms.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
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

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.ContentInqTrf;
import com.bsms.restobjclient.InquiryTrfDispResp;
import com.bsms.restobjclient.InquiryTrfReq;
import com.bsms.restobjclient.InquiryTrfResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbLogUtil;
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
    
    private static Logger log = LoggerFactory.getLogger(MbInquiryTransferServiceImpl.class);
	
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		
		LibFunctionUtil libFunct=new LibFunctionUtil();
		String trx_id=libFunct.getTransactionID(6);
		
		MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
        InquiryTrfReq inquiryTrfReq = new InquiryTrfReq();
        
        inquiryTrfReq.setCorrelationId(trx_id);
		inquiryTrfReq.setTransactionId(trx_id);
        inquiryTrfReq.setDeliveryChannel("6027");
        inquiryTrfReq.setSourceAccountNumber(request.getAccount_number());
        inquiryTrfReq.setSourceAccountName(request.getCustomerName());
        inquiryTrfReq.setDestinationAccountNumber(request.getDestinationAccountNumber());
        inquiryTrfReq.setDestinationAccountName("");
        inquiryTrfReq.setAmount(request.getAmount());	
        inquiryTrfReq.setDescription(request.getDescription());
        inquiryTrfReq.setPan(request.getPan());
        inquiryTrfReq.setCardAcceptorTerminal("00307180");
        inquiryTrfReq.setCardAcceptorMerchantId(request.getMsisdn());
        inquiryTrfReq.setCurrency("360");

        System.out.println(new Gson().toJson(inquiryTrfReq));
        
        try {
			
        	HttpEntity<?> req = new HttpEntity(inquiryTrfReq, RestUtil.getHeaders());
        	RestTemplate restTemps = new RestTemplate();
        	String url = inquiryTransfer;
        	
			ResponseEntity<InquiryTrfResp> response = restTemps.exchange(url, HttpMethod.POST, req, InquiryTrfResp.class);
			InquiryTrfResp inquiryTrfResp = response.getBody();
			
			System.out.println("::: Inquiry Trf From Back End :::");
			System.out.println(new Gson().toJson(response.getBody()));

			
			if("00".equals(inquiryTrfResp.getResponseCode())) {
				
				List<ContentInqTrf> content = new ArrayList<>();
				content.add(new ContentInqTrf("Amount",request.getAmount()));
				content.add(new ContentInqTrf("Description",request.getDescription()));
				
				InquiryTrfDispResp inquiryTrfDispResp = new InquiryTrfDispResp(request.getAccount_number(),
						request.getCustomerName(),
						request.getDestinationAccountNumber(),
						inquiryTrfResp.getContent().getDestinationAccountName(),
								content,trx_id);

				mbApiResp = MbJsonUtil.createResponseTrf(inquiryTrfResp.getResponseCode(),
        				"Success",
        				inquiryTrfDispResp,trx_id); 

				 
				
			} else {
				System.out.println(inquiryTrfResp.getResponseCode() + " <<<========== response code error");
				mbApiResp = MbJsonUtil.createResponseTrf(inquiryTrfResp.getResponseCode(),
    					inquiryTrfResp.getContent().getErrorMessage(),
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
