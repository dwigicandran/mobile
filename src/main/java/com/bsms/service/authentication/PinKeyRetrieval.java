package com.bsms.service.authentication;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

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
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.NotifcglistRepository;
import com.bsms.repository.SecurityRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.authentication.PINKeyReq;
import com.bsms.restobjclient.authentication.PINKeyResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
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

	@SuppressWarnings("unlikely-arg-type")
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {

		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);

		String responseDesc = null;
		String responseCode = null;

		PINKeyResp pinKeyResp = null;
		MbApiResp response = null;

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
			MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600008", "id");
			responseDesc = mbAppContent.getDescription();
			responseCode = "01";
			response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);

		} else {
			try {
				HttpEntity<?> req = new HttpEntity(pinKeyReq, RestUtil.getHeaders());
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<PINKeyResp> responseEntity = restTemplate.exchange(url, HttpMethod.POST, req,
						PINKeyResp.class);

				pinKeyResp = responseEntity.getBody();

				System.out.println(responseEntity.getStatusCodeValue() + " ::: status code");

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
				response = MbJsonUtil.createResponse(request, pinKeyResp.getClearZPK(), pinKeyResp.getResponse(),
						TrxIdUtil.getTransactionID(6), MbApiConstant.SUCCESS_CODE);

			} catch (Exception e) {
				MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
				responseDesc = mbAppContent.getDescription();
				responseCode = "666";
				response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
			}
		}

		txLog.setResponse(response);
		txLogRepository.save(txLog);

		return response;
	}

}
