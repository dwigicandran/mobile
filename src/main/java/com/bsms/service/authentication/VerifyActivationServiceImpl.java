package com.bsms.service.authentication;

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
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.authentication.VerifyPinReq;
import com.bsms.restobjclient.authentication.VerifyPinResp;
import com.bsms.restobjclient.authentication.VerifyPinRespDisp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.LibCNCrypt;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;
import com.google.gson.Gson;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("verifyActivation")
public class VerifyActivationServiceImpl extends MbBaseServiceImpl implements MbService {

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

    MbApiResp response;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {

        MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);

        Integer failedPINCount;
        Boolean isPINValid = false;
        boolean mustUpdate = false;
        Long customerId;

//		String language = MbApiConstant.DEFAULT_LANG;
        String language = request.getLanguage() != null ? request.getLanguage() : "id";
        String responseCode = "";
        String responseDesc = "";
        String msisdn = null;
        String msisdns = null;

        MbApiResp mbApiResp;

        VerifyPinReq verifyPinReq = new VerifyPinReq();

        System.out.println(request.getSessionId() + " ::: SESSION ID");
        System.out.println(request.getCustomer_id() + " ::: CUSTOMER ID");

        Security security = securityRepository.findByMbSessionId(request.getSessionId());
        customerId = security.getCustomerId();
        String ZPK_lmk = security.getZpkLmk();

        CardMapping cardMapping = cardMappingRepository.findTopByCustomerid(Long.parseLong(request.getCustomer_id()));
        System.out.println(cardMapping.getPinoffset() + " :: PIN OFFSET");
        System.out.println(cardMapping.getCardnumber() + " ::: CARD NUMBER");

        String pinOffset = String.format("%-12s", LibCNCrypt.decrypt1(cardMapping.getPinoffset())).replace(" ", "F");
        String cardNumber = cardMapping.getCardnumber();

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

        System.out.println("verify pin" + new Gson().toJson(verifyPinReq));

        try {

            HttpEntity<?> req = new HttpEntity(verifyPinReq, RestUtil.getHeaders());
            RestTemplate restTemps = new RestTemplate();
            ResponseEntity<VerifyPinResp> response = restTemps.exchange(url, HttpMethod.POST, req, VerifyPinResp.class);
            VerifyPinResp verifyPinResp = response.getBody();

            failedPINCount = customerRepository.getFailedPINCountById(customerId);
            if (failedPINCount == null) {
                failedPINCount = 0;
            }

            System.out.println(failedPINCount + " ::: FAILEDPINCOUNT");
            System.out.println(verifyPinResp.getResponseCode() + " ::: RESPONSE HSM");

            if ("00".equals(verifyPinResp.getResponseCode())) {

                if (failedPINCount < 3) {
                    isPINValid = true;
                    mustUpdate = (failedPINCount != 0);
                    failedPINCount = 0;

                    long msisdnDb = customerRepository.getMsisdnByID(customerId);
                    msisdn = String.valueOf(msisdnDb);

                    StringBuilder sb = new StringBuilder();
                    sb.append("0");
                    sb.append(msisdn);
                    msisdns = sb.toString();

//					customerRepository.updatePINCountById(failedPINCount, msisdn);

                    // update via jpa
                    Customer custUpd = customerRepository.findTopByMsisdn(msisdns);
                    custUpd.setFailedpincount(String.valueOf(failedPINCount));
                    customerRepository.save(custUpd);
                    // update via jpa

                    VerifyPinRespDisp display = new VerifyPinRespDisp();
                    display.setTransactionId(TrxIdUtil.getTransactionID(6));

//					verifyPinResp.setTransactionId(TrxIdUtil.getTransactionID(6));
//					mbApiResp = MbJsonUtil.createResponse(request, verifyPinResp, verifyPinResp.getResponse(),
//							TrxIdUtil.getTransactionID(6), verifyPinResp.getResponseCode());

//					mbApiResp = MbJsonUtil.createResponse(request, verifyPinResp, new MbApiStatusResp(MbApiConstant.SUCCESS_CODE,  verifyPinResp.getResponse()),
//							TrxIdUtil.getTransactionID(6), verifyPinResp.getResponseCode());
                    String pinMessage = request.getLanguage().equals("en") ? "Verify PIN Successfull" : "Verifikasi Pin Berhasil";
                    mbApiResp = MbJsonUtil.createResponse(request, display, verifyPinResp.getResponseCode(), pinMessage);


                } else {
                    System.out.println("response code 178 : " + responseCode);
                    responseCode = "38";
                    ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(responseCode, language);
                    mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, errMsg.getDescription());
                }

            } else {
                if ("01".equals(verifyPinResp.getResponseCode())) {

                    long msisdnDb = customerRepository.getMsisdnByID(customerId);
                    msisdn = String.valueOf(msisdnDb);

                    StringBuilder sb = new StringBuilder();
                    sb.append("0");
                    sb.append(msisdn);
                    msisdns = sb.toString();

                    failedPINCount = customerRepository.getFailedPINCountById(customerId);
                    ++failedPINCount;

                    System.out.println(failedPINCount + " :: PIN SALAH");
                    System.out.println(sb.toString() + " ::: MSISDN");

//					customerRepository.updatePINCountById(failedPINCount, msisdns);

                    // update via jpa
                    Customer custUpd = customerRepository.findTopByMsisdn(msisdns);
                    custUpd.setFailedpincount(String.valueOf(failedPINCount));
                    customerRepository.save(custUpd);
                    // update via jpa

                    mustUpdate = true;
                    if (failedPINCount < 3) {
                        responseCode = "55";
                    } else {
                        responseCode = "38";
                    }
                    System.out.println("response code 215: " + responseCode);
                    ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(responseCode, language);
                    mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, errMsg.getDescription());
                } else {
                    System.out.println("response code : " + responseCode);
                    responseCode = "05";
                    ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(responseCode, language);
                    mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, errMsg.getDescription());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("response code 228: " + responseCode);
            MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", language);
            responseDesc = mbAppContent.getDescription();
            responseCode = MbApiConstant.ERR_CODE;
            mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
        }

        return mbApiResp;
    }

}
