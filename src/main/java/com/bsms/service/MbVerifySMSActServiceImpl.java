package com.bsms.service;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbSmsActivation;
import com.bsms.repository.MbSmsActivationRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;

@Service("verifySmsAct")
public class MbVerifySMSActServiceImpl extends MbBaseServiceImpl implements MbService {

	@Autowired
	private MbTxLogRepository txLogRepository;
	
	@Autowired
	private MbSmsActivationRepository smsActRepository;
	
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		
		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);
		
		String responseDesc = null;
		String responseCode = null;
		String language = "id";
		String msisdn = null;
		String isverified = "0";
		
		MbApiResp response = null;
		MbSmsActivation mbSmsActivation = null;
		
		try {
			
			msisdn = request.getMsisdn();
			
			
			mbSmsActivation = smsActRepository.findByMsisdnAndIsverified(msisdn, isverified);
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		

		txLog.setResponse(response);
		txLogRepository.save(txLog);
		
		return response;
	}

	
	
}
