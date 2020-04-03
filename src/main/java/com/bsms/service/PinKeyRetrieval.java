package com.bsms.service;

import java.sql.SQLException;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbAppContent;
import com.bsms.domain.NotifCGList;
import com.bsms.domain.Security;
import com.bsms.except.MbServiceException;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.NotifcglistRepository;
import com.bsms.repository.SecurityRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.PINKeyReq;
import com.bsms.restobjclient.PINKeyResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;

@Service("pinKeyRetrieval")
public class PinKeyRetrieval extends MbBaseServiceImpl implements MbService {

	@Autowired
	private MbTxLogRepository txLogRepository;

	@Autowired
	private SecurityRepository securityRepository;

	@Autowired
	private NotifcglistRepository notifcglistRepository;

	@Autowired
	private MbAppContentRepository mbAppContentRepository;

	@Value("${pinkeyretrieval.url}")
	private String url;

	MbApiResp mbApiResp;
	PINKeyResp pinKeyResp = null;

	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) {

		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);

		try {
			PINKeyReq pinKeyReq = new PINKeyReq();

			pinKeyReq.setCustomerId(request.getCustomerId());
			pinKeyReq.setDateLocal(request.getDateLocal());
			pinKeyReq.setDevice(request.getDevice());
			pinKeyReq.setDeviceType(request.getDevice_type());
			pinKeyReq.setImei(request.getImei());
			pinKeyReq.setIpAddress(request.getIp_address());
			pinKeyReq.setNotifType(request.getNotifType());
			pinKeyReq.setOsType(request.getOsType());
			pinKeyReq.setOsVersion(request.getOsVersion());
			pinKeyReq.setRequestType(request.getRequest_type());
			pinKeyReq.setToken(request.getToken());
			pinKeyReq.setVersionName(request.getVersion_name());
			pinKeyReq.setVersionValue(request.getVersion_value());
			pinKeyReq.setSessionId(request.getSessionId());

			System.out.println(new Gson().toJson(pinKeyReq));
			
			Security secure = securityRepository.findByMbSessionId(request.getSessionId());

			if (secure == null) {
				MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002",
						request.getLanguage());
				String responseDesc = mbAppContent.getDescription();
				String responseCode = MbApiConstant.ERR_CODE;
				mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
			}

			HttpEntity<?> req = new HttpEntity(pinKeyReq, RestUtil.getHeaders());
			RestTemplate restTemps = new RestTemplate();
			ResponseEntity<PINKeyResp> response = restTemps.exchange(url, HttpMethod.POST, req, PINKeyResp.class);
			pinKeyResp = response.getBody();
			
			System.out.println(new Gson().toJson(response));
			
			System.out.println(pinKeyResp.getZpkZmk() + " ::: ZPK ZMK :::");
			System.out.println(pinKeyResp.getResponseCode() + " ::: RESPONSE CODE :::");
			System.out.println(pinKeyResp.getZpkLmk() + " ::: ZPK ZMK :::");
			
			String noHp = request.getMsisdn();
			secure.setZpkLmk(pinKeyResp.getZpkLmk());
			secure.setStatus("1");
			secure.setMbToken(request.getToken());
			secure.setMbDevice(request.getDevice());
			secure.setMbDeviceType(request.getDevice_type());
			securityRepository.save(secure);

			long jumlah = notifcglistRepository.countByMsisdn(noHp);
			if (jumlah == 0) {

				NotifCGList saveNotifCGList = new NotifCGList();
				saveNotifCGList.setMsisdn(request.getMsisdn());
				saveNotifCGList.setIdCg((long) 1);
				notifcglistRepository.save(saveNotifCGList);

			}

			mbApiResp = MbJsonUtil.createResponse(request, pinKeyResp.getClearZPK(), pinKeyResp.getResponse(),
					TrxIdUtil.getTransactionID(6), MbApiConstant.SUCCESS_CODE);

		} catch (Exception e) {
			e.printStackTrace();
			
			MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", request.getLanguage());
			String responseDesc = mbAppContent.getDescription();
			String responseCode = MbApiConstant.ERR_CODE;
			mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
		}

		txLog.setResponse(mbApiResp);
		txLogRepository.save(txLog);

		return mbApiResp;
	}

}
