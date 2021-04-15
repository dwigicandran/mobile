package com.bsms.service.authentication;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.bsms.domain.NotifCGList;
import com.bsms.domain.Security;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.NotifcglistRepository;
import com.bsms.repository.SecurityRepository;
import com.bsms.restobjclient.authentication.PINKeyDispResp;
import com.bsms.restobjclient.authentication.PINKeyReq;
import com.bsms.restobjclient.authentication.PINKeyResp;
import com.bsms.util.RestUtil;

public class PinKeyRetrievalServiceImpl {
	
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
	
	Security secure;

	public PINKeyDispResp getPinKey(PINKeyReq request) {
		
		PINKeyDispResp pinKeyDispResp = new PINKeyDispResp();
		System.out.println(new Gson().toJson(request));
		
		HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
		
		RestTemplate restTemps = new RestTemplate();
		
		ResponseEntity<PINKeyResp> response = restTemps.exchange(url, HttpMethod.POST, req, PINKeyResp.class);
		PINKeyResp pinKeyResp = response.getBody();
		
		System.out.println(new Gson().toJson(pinKeyResp));
		
		secure = securityRepository.findByMbSessionId(request.getSessionId());
		String noHp = request.getMsisdn();
		secure.setZpkLmk(pinKeyResp.getResponse());
		secure.setStatus("1");
		secure.setMbToken(request.getToken());
		secure.setMbDevice(request.getDevice());
		secure.setMbDeviceType(request.getDeviceType());
		securityRepository.save(secure);

		long jumlah = notifcglistRepository.countByMsisdn(noHp);
		if(jumlah == 0) {
			
			NotifCGList saveNotifCGList = new NotifCGList();
			saveNotifCGList.setMsisdn(request.getMsisdn());
			saveNotifCGList.setIdCg((long) 1);
			notifcglistRepository.save(saveNotifCGList);
			
		}
		
		pinKeyResp.getResponseCode();
		
		return pinKeyDispResp;
		
	}
	
}


//public static BalanceInquiryDispResp getBalance(BalanceInquiryReq request) {
//	
//	BalanceInquiryDispResp balanceInquiryDispResp = new BalanceInquiryDispResp();
//	String accountNumber = "";
//	String amount = "";
//	System.out.println(new Gson().toJson(request));
//
//	HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
//
//	RestTemplate restTemps = new RestTemplate();
//
//	String url = "http://localhost:8888/service/inquiryAccountSimulator";
//	ResponseEntity<BalanceInfoResp> response = restTemps.exchange(url, HttpMethod.POST, req, BalanceInfoResp.class);
//	BalanceInfoResp balanceInfoResp = response.getBody();
//
//	System.out.println(new Gson().toJson(balanceInfoResp));
//
//	if ("00".equals(balanceInfoResp.getResponseCode())) {
//
//		for (GIDIEACCTBALTWSDetailType gIDIEACCTBALTWSDetailType : balanceInfoResp.getContent()
//				.getgIDIEACCTBALTWSDetailType()) {
//			for (MIDIEACCTBALTWSDetailType mIDIEACCTBALTWSDetailType : gIDIEACCTBALTWSDetailType
//					.getMidieacctbaltwsDetailType()) {
//
//				if (mIDIEACCTBALTWSDetailType.getAccountNumber() != null
//						&& !mIDIEACCTBALTWSDetailType.getAccountNumber().trim().equals("")) {
//					accountNumber = mIDIEACCTBALTWSDetailType.getAccountNumber();
//				}
//
//				if (mIDIEACCTBALTWSDetailType.getAvailableBalance() != null
//						&& !mIDIEACCTBALTWSDetailType.getAvailableBalance().trim().equals("")) {
//					amount = mIDIEACCTBALTWSDetailType.getAvailableBalance();
//				}
//			}
//		}
//
//		System.out.println(new Gson().toJson(response.getBody()));
//
//		balanceInquiryDispResp.setCorrelationId(balanceInfoResp.getCorrelationId());
//		balanceInquiryDispResp.setTransactionId(TrxIdUtil.getTransactionID(1));
//		balanceInquiryDispResp.setResponse("Saldo anda saat ini pada akun : " + accountNumber + ", " + "sebesar Rp."
//				+ amount
//				+ " \\n\\nVersi baru Mandiri Syariah Mobile Banking sudah tersedia.\\n\\nSilahkan update aplikasi Anda untuk kenyamanan dan kemudahan transaksi Anda.");
//		balanceInquiryDispResp.setShare(
//				"Transaksi Informasi saldo  Informasi Saldo menggunakan aplikasi BSM mobile banking berhasil, download via Google Play Store");
//
//	} else {
//		String responseCode = balanceInfoResp.getContent().getErrorCode();
//		String responseDesc = balanceInfoResp.getContent().getErrorMessage();
//
//		balanceInquiryDispResp.setErrorCode(responseCode);
//		balanceInquiryDispResp.setErrorMessage(responseDesc);
//	}
//
//	return balanceInquiryDispResp;
//
//}
//
