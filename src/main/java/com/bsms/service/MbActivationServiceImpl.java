package com.bsms.service;

import java.security.Key;
import java.security.KeyPair;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.Customer;
import com.bsms.domain.MbActivation;
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbAppContent;
import com.bsms.domain.MbSmsActivation;
import com.bsms.domain.Security;
import com.bsms.repository.CardmappingRepository;
import com.bsms.repository.CustomerRepository;
import com.bsms.repository.MbActivationRepository;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbSmsActivationRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.SecurityRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.ActivationDispResp;
import com.bsms.restobjclient.PINKeyReq;
import com.bsms.restobjclient.PINKeyResp;
import com.bsms.util.GenACUtil;
import com.bsms.util.MbDateFormatUtil;
import com.bsms.util.MbDecryptDesUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLibRSA;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;
import com.google.gson.Gson;
import com.sun.research.ws.wadl.HTTPMethods;

import org.springframework.transaction.annotation.Transactional;

@Service("Activation")
@Transactional
public class MbActivationServiceImpl extends MbBaseServiceImpl implements MbService {

	@Autowired
	private MbTxLogRepository txLogRepository;

	@Autowired
	private MbSmsActivationRepository mbSmsActivationRepository;

	@Autowired
	private SecurityRepository securityRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CardmappingRepository cardMappingRepository;

	@Autowired
	private MbAppContentRepository mbAppContentRepository;

	@Autowired
	private MbActivationRepository MbActivationRepository;

	@Value("${sms.received.timeout}")
	private long timeOut;

	@Value("${pinkeyretrieval.url}")
	private String url;

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {

		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);

		if (request.getMsisdn() == null || "".equals(request.getMsisdn())) {
			throw createSlServiceException(MbApiConstant.ERR_CODE, "msisdn cannot be empty value", txLog,
					txLogRepository);
		}

		if (request.getImei() == null || "".equals(request.getImei())) {
			throw createSlServiceException(MbApiConstant.ERR_CODE, "imei cannot be empty value", txLog,
					txLogRepository);
		}

		String responseDesc = null;
		String responseCode = null;
		String language = "id";
		String msisdn = request.getMsisdn();

		MbApiResp response = null;

		try {

			String req_data = request.getRequest_data(); // TODO: check req data dari depan

			GenACUtil genAcUtil = new GenACUtil();
			String act_code = genAcUtil.genAC(request.getMsisdn());
			if (act_code.equals("")) {

				MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600007", language);
				responseDesc = mbAppContent.getDescription();
				responseCode = MbApiConstant.ERR_CODE;
				response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);

			} else {

				String count = MbActivationRepository.findByMsisdn(request.getMsisdn());
				if (count == null) {
					count = "0";
				}
				int result = Integer.parseInt(count);

				if (result >= 3) {

					MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600014", language);
					responseDesc = mbAppContent.getDescription();
					responseCode = MbApiConstant.ERR_CODE;
					response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);

				} else {

//					System.out.println(act_code + " ::: ACT CODE :::");
//					System.out.println(req_data + " ::: REQ DATA :::");
//					
//					MbDecryptDesUtil mbDecryptDesUtil = new MbDecryptDesUtil();
//					mbDecryptDesUtil.DecryptDes(act_code, req_data);

//					if (!isValidJson(req_data)) {
//
//						MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600019", language);
//						responseDesc = mbAppContent.getDescription();
//						responseCode = MbApiConstant.ERR_CODE;
//						response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
//					} else {

					String msisdn1, msisdn2;

					if ("0".equals(request.getMsisdn().substring(0, 1))) {
						msisdn1 = msisdn;
						msisdn2 = "62" + msisdn.substring(1);
					} else {
						msisdn1 = "0" + msisdn.substring(2);
						msisdn2 = msisdn;
					}

					List<Customer> cust = customerRepository.getByMsisdn(msisdn1, msisdn2);
					Long customerId = null;
					String customerName = null;
					String email = null;
					String tak = null;
					String machex = null;
					String custOtpDate = null;
					String oldActivationCode = null;
					String isReactivation = null;

					for (Customer getCust : cust) {
						customerId = getCust.getId();
						customerName = getCust.getName();
						email = getCust.getEmail();
						tak = getCust.getTak();
						machex = getCust.getMachex();
						custOtpDate = getCust.getCreateotpdate();
						oldActivationCode = getCust.getActivationcode();
					}

					System.out.println(customerId + " ::: CustomerId");
					System.out.println(customerName + " ::: NAMA");
					System.out.println(email + " ::: EMAIL");

					String pinOffset = cardMappingRepository.getPinoffsetByID(Long.toString(customerId));
					if (pinOffset == null) {
						isReactivation = "0"; // activation 2.1 : 
					} else {
						isReactivation = "1"; // activation 2.2
					}

					System.out.println(isReactivation + " ::: pinOffset");

					String isVerified = "0";

					// smsActVerify
					long time = (timeOut / 1000);

					System.out.println(time + " :: TIME");

					String smsMsisdn = null;
					String dateReceived = null;
					List<MbSmsActivation> smsAct;

					Timestamp getDateVerified = new Timestamp(System.currentTimeMillis());

					long startTime = System.currentTimeMillis();
					long duration = 0;
					do {
						smsAct = mbSmsActivationRepository.getDataByMsisdn(msisdn1, msisdn2, isVerified, time);

						for (MbSmsActivation sms : smsAct) {
							smsMsisdn = sms.getMsisdn();
							dateReceived = sms.getDateReceived();

							System.out.println(sms.getMsisdn());
							System.out.println(sms.getDateReceived());
						}

						Thread.sleep(5000);
						duration = System.currentTimeMillis() - startTime;
					} while (duration <= timeOut);

					String dateVerified = String.valueOf(getDateVerified);

					System.out.println(duration + " ::: durasi loop");
					System.out.println(timeOut + " ::: timeout");
					
					System.out.println(smsMsisdn + " ::: msisdn");
					
					if(smsMsisdn != null) {
						
						// update data table
						MbSmsActivation smsActivationUpd = mbSmsActivationRepository.findByMsisdnAndIsverifiedAndDateReceived(smsMsisdn, isVerified, dateReceived);
						System.out.println(smsActivationUpd.getIsverified() + " ::: CEK IS VERIFIED DARI DB");
						System.out.println(smsActivationUpd.getMessage() + " ::: CEK MESSAGE DARI DB");
						
						smsActivationUpd.setIsverified("1");
						smsActivationUpd.setDateVerified(dateVerified);
						mbSmsActivationRepository.save(smsActivationUpd);
						
//						String isVerifiedUpd = "1";
//						mbSmsActivationRepository.updateSmsAct(isVerifiedUpd, dateVerified, smsMsisdn, isVerified, dateReceived);
						
						mbSmsActivationRepository.updateSmsAct(dateVerified, smsMsisdn, isVerified, dateReceived);
						
						/////////////////////////////////////////////////
						
						System.out.println(dateVerified + " ::: date verified ");
						
						// request ke hsm pinkeyRetrieval
						PINKeyResp pinKeyResp;
						
						PINKeyReq pinKeyReq = new PINKeyReq();
						pinKeyReq.setCustomerId(Long.toString(customerId));
						
						System.out.println(new Gson().toJson(pinKeyReq));
						
						try {
							
							HttpEntity<?> req = new HttpEntity(pinKeyReq, RestUtil.getHeaders());
							RestTemplate restTemplate = new RestTemplate();
							ResponseEntity<PINKeyResp> responseEntity = restTemplate.exchange(url, HttpMethod.POST, req,
									PINKeyResp.class);
							pinKeyResp = responseEntity.getBody();
							
							System.out.println(new Gson().toJson(responseEntity));
							
							String clearZPK = pinKeyResp.getClearZPK();
							String zpkLmk = pinKeyResp.getZpkLmk();
							
							// delete customer by customerId
							System.out.println(customerId + " ::: cust id di security");
							securityRepository.deleteByCustId(customerId);
							
							// delete msisdn dari table mb_activation
							MbActivationRepository.deleteByMsisdn(smsMsisdn);
							
							Timestamp ts=new Timestamp(System.currentTimeMillis());  
				            Date changeTime=ts;
				            String sessionId = TrxIdUtil.getTransactionID(6);
				            System.out.println(sessionId + " :: SESSION ID");
				            System.out.println(msisdn + " ::: MSISDN");
				            
				            MbLibRSA lib_rsa = new MbLibRSA("RSA/None/PKCS1Padding");
				            lib_rsa.GenerateKeypair();
				            String public_key = lib_rsa.GetPublicKeyPem();
		                    String private_key = lib_rsa.GetPrivateKeyPem();

				            //TODO: mandatory field : customerId, status, changeTime, privateKey
							Security securitySave = new Security();
							securitySave.setCustomerId(customerId);
							securitySave.setZpkLmk(zpkLmk);
							securitySave.setStatus("1");
							securitySave.setChangeTime(changeTime);
							securitySave.setMbDevice(request.getDevice());
							securitySave.setMbDeviceType(request.getDevice_type());
							securitySave.setMbIpAddress(request.getIp_address());
							securitySave.setMbImei(request.getImei());
							securitySave.setMbIccid(request.getIccid());
							securitySave.setMbSessionId(sessionId);
							securitySave.setPrivateKey(private_key);
							securitySave.setMb_PublicKey(public_key);
							securityRepository.save(securitySave);
							
							ActivationDispResp activationDispResp = new ActivationDispResp();
							
							activationDispResp.setCustomerId(Long.toString(customerId));
							activationDispResp.setName(customerName);
							activationDispResp.setClearZpk(clearZPK);
							activationDispResp.setResponseCode(MbApiConstant.SUCCESS_CODE);
							activationDispResp.setPublicKey(public_key);
							activationDispResp.setSessionId(sessionId);
							activationDispResp.setIsReactivation(isReactivation);
							activationDispResp.setEmail(email);
							activationDispResp.setMsisdn(smsMsisdn);
							
							response = MbJsonUtil.createResponse(request, activationDispResp,
			    					new MbApiStatusResp(MbApiConstant.SUCCESS_CODE, MbApiConstant.OK_MESSAGE), MbApiConstant.SUCCESS_CODE, MbApiConstant.SUCCESS_MSG);
			        		
							
						} catch(Exception e) {
							MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
			    			responseDesc = mbAppContent.getDescription();
			    			responseCode = MbApiConstant.ERR_CODE;
			    			response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
						}
					}
					else {
						System.out.println(" MSISDN NOT FOUND");
						
						// remove dari String sql = "DELETE FROM MB_Activation where msisdn='" + msisdn + "'";
						
						MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
		    			responseDesc = mbAppContent.getDescription();
		    			responseCode = MbApiConstant.ERR_CODE;
		    			response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
					}
					
				}
//				}
			}

		} catch (Exception e) {
			MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
			responseDesc = mbAppContent.getDescription();
			responseCode = MbApiConstant.ERR_CODE;
			response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
		}

		txLog.setResponse(response);
		txLogRepository.save(txLog);

		return response;
	}

	public boolean isValidJson(String json) {
		if (json.isEmpty()) {
			return false;
		}
		try {
			JSONObject jsonObject = new JSONObject(json);
		} catch (JSONException Ex) {

			try {
				JSONArray jsonArray = new JSONArray(json);
			} catch (JSONException ex) {
				return false;
			} catch (Exception ex) {
				return false;
			}

		}

		return true;
	}

}
