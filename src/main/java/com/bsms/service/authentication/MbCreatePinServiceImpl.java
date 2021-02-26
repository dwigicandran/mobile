package com.bsms.service.authentication;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbAppContent;
import com.bsms.domain.Security;
import com.bsms.repository.CardmappingRepository;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.SecurityRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.authentication.CreatePinReq;
import com.bsms.restobjclient.authentication.CreatePinResp;
import com.bsms.restobjclient.authentication.CreatePinRespDisp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.LibCNCrypt;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;
import com.google.gson.Gson;

@Service("createPin")
@Transactional
public class MbCreatePinServiceImpl extends MbBaseServiceImpl implements MbService {

	@Autowired
	private MbAppContentRepository mbAppContentRepository;

	@Autowired
	private MbTxLogRepository txLogRepository;

	@Autowired
	private SecurityRepository securityRepository;

	@Autowired
	private CardmappingRepository cardMappingRepository;

	@Value("${createpin.url}")
	private String url;

	MbApiResp response;

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {

		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);

		String responseCode = "";
		String responseDesc = "";
		String pan 			= "";
		String customerId 	= "";
		String zpk 			= "";
		String language 	= "";
		String pin 			= request.getPin();
		
		if("".equals(request.getLanguage())) {
			language = MbApiConstant.DEFAULT_LANG;
		}
		
		Security security = securityRepository.findByMbSessionId(request.getSessionId());
		customerId = security.getCustomerId().toString();
		zpk = security.getZpkLmk();

		Integer count = cardMappingRepository.getCountByCustomerId(customerId);
		System.out.println(count + " ::: count ");
				
		if(count > 0) {
			throw createSlServiceException(MbApiConstant.ERR_CODE, "Already Have PIN", txLog,
					txLogRepository);
		}
		
		if (count > 0) { 

		} else {
			pan = cardMappingRepository.getCardnumberByCustomerId(customerId);
			System.out.println(pan + " ::: pan");
		}

		if (!"".equals(pan)) { // validate pin exist or not 

			CreatePinResp createPinResp;
			CreatePinReq createPinReq = new CreatePinReq();

			createPinReq.setZpk(zpk);
			createPinReq.setPin(pin);
			createPinReq.setCardNumber(pan);

			System.out.println(new Gson().toJson(createPinReq));

			try {

				// connect to hsm to create pin
				HttpEntity<?> req = new HttpEntity(createPinReq, RestUtil.getHeaders());
				RestTemplate restTemplate = new RestTemplate();
				ResponseEntity<CreatePinResp> responseEntity = restTemplate.exchange(url, HttpMethod.POST, req,
						CreatePinResp.class);
				createPinResp = responseEntity.getBody();

				System.out.println(new Gson().toJson(responseEntity));

				if ("00".equals(createPinResp.getResponse_code())) {
					
					CreatePinRespDisp createPinRespDisp = new CreatePinRespDisp();
					
					String pinoffset = LibCNCrypt.encrypt1(createPinResp.getPinoffset());
					System.out.println(createPinResp.getPinoffset() + " ::: pin offset");
					System.out.println(pinoffset + " ::: pin offset yang baru");
					cardMappingRepository.updPinoffsetByCardnum(pinoffset, pan); // update pin offset
					
					createPinRespDisp.setTransactionId(TrxIdUtil.getTransactionID(6));
					
					response = MbJsonUtil.createResponse(request, createPinRespDisp, createPinResp.getResponse_code(), "Create PIN succesfull");

				} else if("ZZ".equals(createPinResp.getResponse_code())) {
					MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", language);
					responseDesc = mbAppContent.getDescription();
					responseCode = MbApiConstant.ERR_CODE;
					response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
				} else {
					
					MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600020", language);
					responseDesc = mbAppContent.getDescription();
					responseCode = MbApiConstant.ERR_CODE;
					response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
					
				}

			} catch (Exception e) {
				e.printStackTrace();
				MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", language);
				responseDesc = mbAppContent.getDescription();
				responseCode = MbApiConstant.ERR_CODE;
				response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
			}

		} else {
			MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", language);
			responseDesc = mbAppContent.getDescription();
			responseCode = MbApiConstant.ERR_CODE;
			response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
		}

		return response;
	}

}
