package com.bsms.service;

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
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.ActivationReq;
import com.bsms.restobjclient.ActivationResp;
import com.bsms.restobjclient.PINKeyResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;

@Service("activation")
public class MbActivation implements MbService {

	@Autowired
    private MbTxLogRepository txLogRepository;

    @Value("${activation.url}")
	private String url;
    
    MbApiResp mbApiResp;
	
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		
		MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);
        
        try {
			
        	ActivationReq activationReq = new ActivationReq();
        	activationReq.setActivationCode(request.getActivationCode());
        	activationReq.setDevice(request.getDevice());
        	activationReq.setDeviceType(request.getDevice_type());
        	activationReq.setImei(request.getImei());
        	activationReq.setIpAddress(request.getIp_address());
        	activationReq.setMsisdn(request.getMsisdn());
        	activationReq.setOsType(request.getOsType());
        	activationReq.setOsVersion(request.getOsVersion());
        	activationReq.setOtp(request.getOtp());
        	activationReq.setPublic_key(request.getPublic_key());
        	activationReq.setRequest_type(request.getRequest_type());
        	activationReq.setVersionName(request.getVersion_name());
        	activationReq.setVersionValue(request.getVersion_value());
        	
        	HttpEntity<?> req = new HttpEntity(activationReq, RestUtil.getHeaders());
			RestTemplate restTemps = new RestTemplate();
			ResponseEntity<ActivationResp> response = restTemps.exchange(url, HttpMethod.POST, req, ActivationResp.class);
			ActivationResp activationResp = response.getBody();
			
			// mbApiResp = MbJsonUtil.createResponse(request, pinKeyResp.getClearZPK(), pinKeyResp.getResponse(), TrxIdUtil.getTransactionID(6), pinKeyResp.getResponseCode());
			mbApiResp = MbJsonUtil.createResponse(request, activationResp,
					new MbApiStatusResp(MbApiConstant.SUCCESS_CODE, MbApiConstant.OK_MESSAGE));
        	
		} catch (Exception e) {
			e.printStackTrace();
			mbApiResp = MbJsonUtil.createExceptionSL(request, e);
		}
		
        txLog.setResponse(mbApiResp);
		txLogRepository.save(txLog);
		
		return mbApiResp;
	}

}
