package com.bsms.service;

import java.sql.Timestamp;
import java.util.Date;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.BodyInqMUBP;
import com.bsms.restobjclient.InquiryMUBPReq;
import com.dto.inquirymubp.SoaHeader;

//TODO : validasi request dan response UBP
public class MbInquiryMUBPServiceImpl extends MbBaseServiceImpl implements MbService {

	@Value("${core.service.inqMubp}")
	private String url;
	
	@Autowired
	private MbTxLogRepository txLogRepository;
	
	MbApiResp response;
	
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		
		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);
		
		 Date date= new Date();
		 long time = date.getTime();
		 Timestamp ts = new Timestamp(time);
		
		SoaHeader soaHeader = new SoaHeader();
//		soaHeader.setChannelId(channelId);
//		soaHeader.setExternalId(externalId);
//		soaHeader.setJournalSequence(journalSequence);
//		soaHeader.setTransactionCode(transactionCode);
		soaHeader.setTimestamp(ts.toString());
		
		
		/*
		
		private String companyCode;
		private String channelID;
		private String customerAccountNumber;
		private String traceNumber;
		private String cardAcceptorTermId;
		private String track2data;
		private String languageCode;
		private String currencyCode;
		private String billKey1;
		
		 * */
		
		BodyInqMUBP bodyReq = new BodyInqMUBP();
//		bodyReq.setCompanyCode(companyCode);
//		bodyReq.setChannelID(channelID);
//		bodyReq.setCustomerAccountNumber(customerAccountNumber);
//		bodyReq.setTraceNumber(traceNumber);
//		bodyReq.setCardAcceptorTermId(cardAcceptorTermId);
//		bodyReq.setTrack2data(track2data);
//		bodyReq.setLanguageCode(languageCode);
//		bodyReq.setCurrencyCode(currencyCode);
//		bodyReq.setBillKey1(billKey1);
		
		InquiryMUBPReq req = new InquiryMUBPReq();
		req.setSoaHeader(soaHeader);
		req.setCustomerId(request.getCustomerId());
		
		
		txLog.setResponse(response);
		txLogRepository.save(txLog);
		
		return response;
	}

}
