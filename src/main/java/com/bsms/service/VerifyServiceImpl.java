package com.bsms.service;

import java.util.Optional;

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
import com.bsms.domain.CardMapping;
import com.bsms.domain.Customer;
import com.bsms.domain.ErrorMessage;
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbAppContent;
import com.bsms.domain.Security;
import com.bsms.repository.CardmappingRepository;
import com.bsms.repository.CustomerRepository;
import com.bsms.repository.ErrormsgRepository;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.SecurityRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.PINKeyResp;
import com.bsms.restobjclient.VerifyPinReq;
import com.bsms.restobjclient.VerifyPinResp;
import com.bsms.util.LibCNCrypt;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;
import com.google.gson.Gson;

@Service("verify")
public class VerifyServiceImpl implements MbService {

	@Autowired
	private MbTxLogRepository txLogRepository;

	@Autowired
	private SecurityRepository securityRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private MbAppContentRepository mbAppContentRepository;

	@Autowired
	private ErrormsgRepository errormsgRepository;

	@Autowired
	private CardmappingRepository cardMappingRepository;

	@Value("${verify.url}")
	private String url;

	MbApiResp mbApiResp;

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {

		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);

		int failedPINCount = 0;
		Boolean isPINValid = false;
		boolean mustUpdate = false;
		String responseCode = "";
		String responseDesc = "";
		String language = "id";
		Long customerId;
		String msisdn;

		try {

			VerifyPinReq verifyPinReq = new VerifyPinReq();

			Security security = securityRepository.findByMbSessionId(request.getSessionId());
			customerId = security.getCustomerId();
			String ZPK_lmk = security.getZpkLmk();

			System.out.println(customerId + " ::: CUSTOMER ID ::: ");
			System.out.println(request.getAccount_number() + " ::: ACCOUNTNUMBER ::: ");

			CardMapping cardMapping = cardMappingRepository.findByCustomeridAndAccountnumber(customerId,
					request.getAccount_number());

			System.out.println(cardMapping.getPinoffset() + " ::: PIN OFFSET :::");
			System.out.println(cardMapping.getCardnumber() + " ::: CARD NUMBER :::");

			String pinOffset = String.format("%-12s", LibCNCrypt.decrypt1(cardMapping.getPinoffset())).replace(" ",
					"F");
			String cardNumber = cardMapping.getCardnumber();

			System.out.println(pinOffset + " ::: PIN OFFSET DECRYPT1 :::");
			System.out.println(ZPK_lmk + "");

			verifyPinReq.setDevice(request.getDevice());
			verifyPinReq.setDeviceType(request.getDevice_type());
			verifyPinReq.setImei(request.getImei());
			verifyPinReq.setIpAddress(request.getIp_address());
			verifyPinReq.setOsType(request.getOsType());
			verifyPinReq.setOsVersion(request.getOsVersion());
			verifyPinReq.setPin(request.getPin());
			verifyPinReq.setRequestType(request.getRequest_type());
			verifyPinReq.setVersionName(request.getVersion_name());
			verifyPinReq.setVersionValue(request.getVersion_value());
			verifyPinReq.setCard_number(cardNumber);
			verifyPinReq.setPin_offset(pinOffset);
			verifyPinReq.setZpk(ZPK_lmk);
			verifyPinReq.setSessionId(request.getSessionId());
			verifyPinReq.setModulId(request.getModul_id());
			verifyPinReq.setSrcAcc(request.getSourceAccountNumber());

			System.out.println(new Gson().toJson(verifyPinReq));

			HttpEntity<?> req = new HttpEntity(verifyPinReq, RestUtil.getHeaders());
			RestTemplate restTemps = new RestTemplate();
			ResponseEntity<VerifyPinResp> response = restTemps.exchange(url, HttpMethod.POST, req, VerifyPinResp.class);
			VerifyPinResp verifyPinResp = response.getBody();

			long failedPin = customerRepository.countByMsisdn(request.getMsisdn());
			System.out.println(failedPin + " ::: COUNT RECORD :::");
			System.out.println(verifyPinResp.getResponseCode() + " ::: RESPONSE CODE DARI HSM ::: ");

			Optional<Customer> customer = customerRepository.findById(customerId);
			msisdn = customer.get().getMsisdn();
			failedPINCount = customer.get().getFailedpincount();

			System.out.println(customer.get().getMsisdn() + " ::: MSISDN ::: ");

			if ("00".equals(verifyPinResp.getResponseCode())) {

				if(failedPINCount < 3) {
					isPINValid = true;
					mustUpdate = (failedPINCount != 0);
					failedPINCount = 0;

					// update failedPINCOUNT jadi 0
					Customer customers = customerRepository.findByMsisdn(msisdn);
					customers.setFailedpincount(failedPINCount);
					customerRepository.save(customers);

					// set response
					verifyPinResp.setResponse("Verify PIN succesfull");
					System.out.println(verifyPinResp.getResponse() + " ::: MESSAGE RESPONSE");
					mbApiResp = MbJsonUtil.createResponse(request, verifyPinResp, verifyPinResp.getResponse(),
							TrxIdUtil.getTransactionID(6), verifyPinResp.getResponseCode());
				} else {
					responseCode = "38";
					ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(responseCode, language);
					mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, errMsg.getDescription());
				}
				
			} else {
				if ("01".equals(verifyPinResp.getResponseCode())) {

					// update failed PIN count
					Customer customers = customerRepository.findByMsisdn(msisdn);
					failedPINCount = customers.getFailedpincount();
					++failedPINCount;
					customers.setFailedpincount(failedPINCount);

					System.out.println(customers.getFailedpincount() + " ::: FAILED COUNT ::: ");
					System.out.println(failedPINCount + " ::: FAILED PIN COUNT ::: ");
					customerRepository.save(customers);

					mustUpdate = true;
					// failedPINCount++;
					if (failedPINCount < 3) {
						responseCode = "55";
					} else {
						responseCode = "38";
					}

					ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(responseCode, language);
					mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, errMsg.getDescription());
				} else {
					responseCode = "05";
					ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(responseCode, language);
					mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, errMsg.getDescription());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();

			MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
			responseDesc = mbAppContent.getDescription();
			responseCode = MbApiConstant.ERR_CODE;
			mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
		}

		return mbApiResp;
	}

}
