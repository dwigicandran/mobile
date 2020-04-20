package com.bsms.service;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import com.bsms.domain.MbAppContent;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.OnlineStatementDispResp;
import com.bsms.restobjclient.OnlineStatementReq;
import com.bsms.restobjclient.OnlineStatementResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;
import com.google.gson.Gson;

@Service("onlineStatementByDate")
public class MbStatementOnlineByDateServiceImpl extends MbBaseServiceImpl implements MbService {

	@Value("${core.service.statementonline}")
	private String statementOnline;

	@Value("${core.uid2}")
	private String coreUid;

	@Value("${core.pass2}")
	private String corePass;

	@Value("${core.company2}")
	private String coreCompany;

	@Autowired
	private MbTxLogRepository txLogRepository;

	@Autowired
	private MbAppContentRepository mbAppContentRepository;

	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

	RestTemplate restTemplate = new RestTemplate();

	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {

		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);

		String responseDesc = null;
		String responseCode = null;
		String startDate = null;
		String endDate = null;

		MbApiResp mbApiResp;
		
		OnlineStatementReq onlineStatementReq = new OnlineStatementReq();

		onlineStatementReq.setCorrelationId(request.getCorrelation_id());
		onlineStatementReq.setTransactionId(TrxIdUtil.getTransactionID(6));
		onlineStatementReq.setCoreUsername(coreUid);
		onlineStatementReq.setCorePassword(corePass);
		onlineStatementReq.setCoreCompany(coreCompany);
		onlineStatementReq.setAccountNumber(request.getAccount_number());
		onlineStatementReq.setStartDate(request.getStart_date());
		onlineStatementReq.setEndDate(request.getEnd_date());

		startDate = request.getStart_date();
		endDate = request.getEnd_date();

		Date startDates = formatter.parse(startDate);
		Date endDates = formatter.parse(endDate);

		long diff = endDates.getTime() - startDates.getTime();
		long diffMonths = (long) (diff / (60 * 60 * 1000 * 24 * 30.41666666));

		System.out.print(diffMonths + " month, ");

		if (diffMonths >= 1) {
			// TODO: throw error
			// throw createSlServiceException("99", "Range Tanggal Tidak Boleh Lebih dari 1
			// Bulan", txLog, txLogRepository);
			responseDesc = "Range Tanggal Tidak Boleh Lebih dari 1 Bulan";
			responseCode = MbApiConstant.ERR_CODE;
			mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
		} else {
			System.out.println(new Gson().toJson(onlineStatementReq));

			try {

				HttpEntity<?> req = new HttpEntity(onlineStatementReq, RestUtil.getHeaders());

				RestTemplate restTemps = new RestTemplate();

				String url = statementOnline;
				ResponseEntity<OnlineStatementResp> response = restTemps.exchange(url, HttpMethod.POST, req,
						OnlineStatementResp.class);

				OnlineStatementResp onlineStatementResp = response.getBody();
				System.out.println(new Gson().toJson(response.getBody()));

				if ("00".equals(onlineStatementResp.getResponseCode())) {

					OnlineStatementDispResp onlineStatementDispResp = new OnlineStatementDispResp();

					onlineStatementDispResp.setTransactionId(TrxIdUtil.getTransactionID(6));
					onlineStatementDispResp.setPeriode(onlineStatementResp.getContent().getPeriode());
					onlineStatementDispResp.setyHEAD1FIX(onlineStatementResp.getContent().getyHEAD1FIX());
					onlineStatementDispResp.setAccountName(onlineStatementResp.getContent().getAccountName());
					onlineStatementDispResp.setCustAdd(onlineStatementResp.getContent().getCustAdd());
					onlineStatementDispResp.setCustAdd2(onlineStatementResp.getContent().getCustAdd2());
					onlineStatementDispResp.setCustAdd3(onlineStatementResp.getContent().getCustAdd3());
					onlineStatementDispResp.setSaldoAwal(onlineStatementResp.getContent().getSaldoAwal());
					onlineStatementDispResp.setTotalDebet(onlineStatementResp.getContent().getTotalDebet());
					onlineStatementDispResp.setTotalKredit(onlineStatementResp.getContent().getTotalKredit());
					onlineStatementDispResp.setSaldoAkhir(onlineStatementResp.getContent().getSaldoAkhir());

					onlineStatementDispResp.setDetailTransaksi(onlineStatementResp.getContent().getDetailTransaksi());

					mbApiResp = MbJsonUtil.createResponse(request, onlineStatementDispResp,
							new MbApiStatusResp(onlineStatementResp.getResponseCode(), MbApiConstant.OK_MESSAGE),
							onlineStatementResp.getResponseCode(), MbApiConstant.SUCCESS_MSG);

				} else {

					MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
					responseDesc = mbAppContent.getDescription();
					responseCode = MbApiConstant.ERR_CODE;
					mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
				}

			} catch (Exception e) {
				MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", "id");
				responseDesc = mbAppContent.getDescription();
				responseCode = MbApiConstant.ERR_CODE;
				mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
			}
		}

		txLog.setResponse(mbApiResp);
		txLogRepository.save(txLog);

		return mbApiResp;
	}

}
