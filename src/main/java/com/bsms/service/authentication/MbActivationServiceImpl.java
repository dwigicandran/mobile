package com.bsms.service.authentication;


import com.bsms.cons.MbApiConstant;
import com.bsms.domain.*;
import com.bsms.repository.*;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.authentication.ActivationDispResp;
import com.bsms.restobjclient.authentication.PINKeyReq;
import com.bsms.restobjclient.authentication.PINKeyResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.*;
import com.fst.HSMConfig;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

	@Autowired
	private SettingRepository settingRepository;

	@Value("${sms.received.timeout}")
	private long timeOut;

	@Value("${pinkeyretrieval.url}")
	private String url;

	@Value("${host.hsm}")
	private String host_hsm;
	@Value("${port.hsm}")
	private String port_hsm;
	@Value("${cZMK.hsm}")
	private String cZMK_hsm;
	@Value("${zmk.hsm}")
	private String zmk_hsm;
	@Value("${pvk.hsm}")
	private String pvk_hsm;
	@Value("${dec.hsm}")
	private String dec_hsm;
	@Value("${pvd.hsm}")
	private String pvd_hsm;
	@Value("${zpk_lmk.hsm}")
	private String zpk_lmk_hsm;
	@Value("${zpk_zmk.hsm}")
	private String zpk_zmk_hsm;
	@Value("${swZPK.hsm}")
	private String swZPK_hsm;
	@Value("${otpTO}")
	private String otpTO;
	@Value("${firstActOtpTO}")
	private String firstActOtpTO;

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
		String language = request.getLanguage();
		String msisdn = request.getMsisdn();

		MbApiResp response = null;

		String req_data = request.getRequest_data(); // TODO: check req data dari depan

		try {

			GenACUtil genAcUtil = new GenACUtil();
			String act_code = genAcUtil.genAC(request.getMsisdn());
			if (act_code.equals("")) {

				MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600007", language);
				responseDesc = mbAppContent.getDescription();
				responseCode = MbApiConstant.ERR_CODE;
				response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);

			}
			else {

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

				}
				else {

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
					String createDate = null;
					String oldActivationCode = null;
					String isReactivation = null;

					for (Customer getCust : cust) {
						customerId = getCust.getId();
						customerName = getCust.getName();
						email = getCust.getEmail();
						tak = getCust.getTak();
						machex = getCust.getMachex();
						custOtpDate = getCust.getCreateotpdate();
						createDate = getCust.getCreatedate();
						oldActivationCode = getCust.getActivationcode();
					}


					System.out.println(customerId + " ::: CustomerId");
					System.out.println(oldActivationCode + " ::: ActivationCode");
					System.out.println(custOtpDate + " ::: CustomerOTPDate");
					System.out.println(customerName + " ::: NAMA");
					System.out.println(email + " ::: EMAIL");
					System.out.println(createDate + " ::: CreateDate");


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

							if(smsMsisdn != null) {
								break;
							}

							System.out.println(sms.getDateReceived());
						}

						System.out.println(smsMsisdn);
						if(smsMsisdn != null) {
							System.out.println(smsMsisdn);
							break;
						}

						Thread.sleep(5000);
						duration = System.currentTimeMillis() - startTime;
					} while (duration <= timeOut);

					String dateVerified = String.valueOf(getDateVerified);

					System.out.println(duration + " ::: durasi loop");
					System.out.println(timeOut + " ::: timeout");
					System.out.println(smsMsisdn + " ::: msisdn");

					if(smsMsisdn != null) {
						//verify OTP
						String OTP = request.getOtp();
						Boolean isOTPValid = false;

						if (custOtpDate == null) {
							// For existing customer to be able to activate
							isOTPValid = oldActivationCode.equalsIgnoreCase(OTP);
							if (isOTPValid) {

							} else {
								MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600019", language);
								responseDesc = mbAppContent.getDescription();
								responseCode = "01";
								response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
							}
						}
						else {
							org.json.me.JSONObject json_request = new org.json.me.JSONObject();

							json_request.put("TAK", tak);
							json_request.put("MACHEX", machex);
							json_request.put("OTP", OTP);

							int port = Integer.parseInt(port_hsm);

							HSMConfig config = new HSMConfig(host_hsm, port, cZMK_hsm, zmk_hsm, pvk_hsm, dec_hsm, pvd_hsm, zpk_lmk_hsm, zpk_zmk_hsm, swZPK_hsm);
							com.fst.OTP otp = new com.fst.OTP(config);
							String response_iso = otp.verifyOTP(json_request).toString();

							if (isValidJson(response_iso)) {

								JSONObject json_iso = new JSONObject(response_iso);
								String response_code = json_iso.getString("DE39");
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								Date custOtpDates = formatter.parse(custOtpDate);
								Date createDates = formatter.parse(createDate);
								Date currDate = new Date();
								//get value setting
								String properties = String.valueOf(settingRepository.getValueByName("onetime.ac"));
								System.out.println(custOtpDates + " ::: createOtpDates");
								System.out.println(createDates + " ::: createDates");
								System.out.println(currDate + " ::: currdate");
								System.out.println(properties +" ::: value setting");

								if (response_code.equals("00")) {
									if ("0".equals(properties)) {
										if ("0".equalsIgnoreCase(isReactivation)) {
											if (((currDate.getTime() - custOtpDates.getTime()) / 60000) > Integer.parseInt(otpTO)) {
												MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600018", language);
												responseDesc = mbAppContent.getDescription();
												responseCode = "68";
												response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);

											}
											else {
												isOTPValid = true;

											}
										} else {
											isOTPValid = true;
										}

									}
									else {
										System.out.println("prop=1");
										if ("1".equalsIgnoreCase(isReactivation)) {
											if (((currDate.getTime() - custOtpDates.getTime()) / 60000) > Integer.parseInt(otpTO)) {
												MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600093", language);
												responseDesc = mbAppContent.getDescription();
												responseCode = "68";
												response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
											}
											else {
												isOTPValid = true;
											}
										}
										else {
											String secure = securityRepository.getChangeTimeByCustomerId(customerId);
											System.out.println(secure+" ::: secure");


											if (secure == null){
												if (((currDate.getTime() - custOtpDates.getTime()) / 60000) > Integer.parseInt(otpTO)) {
													MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600018", language);
													responseDesc = mbAppContent.getDescription();
													responseCode = "68";
													response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
												}
												else {
													isOTPValid = true;
												}
											}
											else {
												Date changeTime = formatter.parse(secure);
												System.out.println(changeTime+" ::: changetime");
//												Date changeTime = formatter.parse(changeTimeSecure);
												if (((currDate.getTime() - changeTime.getTime()) / 3600000) > Integer.parseInt(firstActOtpTO)) {
													MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600018", language);
													responseDesc = mbAppContent.getDescription();
													responseCode = "68";
													response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
												}
												else {
													isOTPValid = true;
												}
											}
										}
									}

								}
								else {

									if ("ZZ".equalsIgnoreCase(response_code)) {
										MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600002", language);
										responseDesc = mbAppContent.getDescription();
										responseCode = MbApiConstant.ERR_CODE;
										response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
									} else {
										MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600019", language);
										responseDesc = mbAppContent.getDescription();
										responseCode = "01";
										response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
									}
								}
							}

						}

						System.out.println("isotpvalid ;;; "+isOTPValid);
						if (isOTPValid) {

							// update data table
							MbSmsActivation smsActivationUpd = mbSmsActivationRepository.findByMsisdnAndIsverifiedAndDateReceived(smsMsisdn, isVerified, dateReceived);
							System.out.println(smsActivationUpd.getIsverified() + " ::: CEK IS VERIFIED DARI DB");
							System.out.println(smsActivationUpd.getMessage() + " ::: CEK MESSAGE DARI DB");

							smsActivationUpd.setIsverified("1");
							smsActivationUpd.setDateVerified(dateVerified);
							mbSmsActivationRepository.save(smsActivationUpd);

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

								//update  customer createOtpDate
								customerRepository.updateCreateOtpDateByMsisdn(msisdn1,msisdn2);

								Timestamp ts = new Timestamp(System.currentTimeMillis());
								Date changeTime = ts;
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



							}
							catch (Exception e) {
								MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600002", language);
								responseDesc = mbAppContent.getDescription();
								responseCode = MbApiConstant.ERR_CODE;
								response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
							}
						}
						else{

							MbActivationRepository.deleteByMsisdn(smsMsisdn);
							System.out.println("Total attempt: " + count);
							String total_count = String.valueOf(Integer.parseInt(count) + 1);
							MbActivationRepository.saveActivation(smsMsisdn,total_count);


						}
					}
					else {
						System.out.println(" MSISDN NOT FOUND");

						// remove dari String sql = "DELETE FROM MB_Activation where msisdn='" + msisdn + "'";

						MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600052", language);
						responseDesc = mbAppContent.getDescription();
						responseCode = MbApiConstant.ERR_CODE;
						response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
					}
				}
			}

		}
		catch (Exception e) {
			MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600019", language);
			responseDesc = mbAppContent.getDescription();
			responseCode = MbApiConstant.ERR_CODE;
			response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
		}

		txLog.setResponse(response);
		txLogRepository.save(txLog);

		return response;
	}


	private boolean isValidJson(String json) {
		if (json.isEmpty()) {
			return false;
		} else {
			try {
				new JSONObject(json);
			} catch (JSONException var6) {
				try {
					new JSONArray(json);
				} catch (JSONException var4) {
					return false;
				} catch (Exception var5) {
					return false;
				}
			}
			return true;
		}
	}



}
