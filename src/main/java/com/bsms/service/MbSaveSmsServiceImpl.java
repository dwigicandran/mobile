package com.bsms.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.sql.DataSource;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbAppContent;
import com.bsms.domain.MbSmsActivation;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbSmsActivationRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.util.MbJsonUtil;

import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("saveSMSActivation")
public class MbSaveSmsServiceImpl extends MbBaseServiceImpl implements MbService {

	@Autowired
	private MbTxLogRepository txLogRepository;

	@Autowired
	private MbAppContentRepository mbAppContentRepository;
	
	@Autowired
	private MbSmsActivationRepository mbSmsActivationRepository;

	@Autowired
	private DataSource dataSource;

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {

		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);

		String responseDesc = null;
		String responseCode = null;
		String phoneNum = null;
		String message = null;

		String language = "id";

		MbApiResp response = null;

		try {

			String requestData = request.getRequest_data();
			String[] arrRequestData = requestData.split(";");
			System.out.println(Arrays.deepToString(arrRequestData));

			phoneNum = arrRequestData[0];
			message = arrRequestData[1];

			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date)); // 2016/11/16 12:08:43
			
			MbSmsActivation mbSmsActivation = new MbSmsActivation();
			mbSmsActivation.setMsisdn(phoneNum);
			mbSmsActivation.setMessage(message);
			mbSmsActivationRepository.saveSms(phoneNum, message);

			MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600094", language);
			responseDesc = mbAppContent.getDescription();
			responseCode = MbApiConstant.SUCCESS_CODE;
			response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);

		} catch (Exception e) {
			e.printStackTrace();

			MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", language);
			responseDesc = mbAppContent.getDescription();
			responseCode = MbApiConstant.ERR_CODE;
			response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
		} 

		txLog.setResponse(response);
		txLogRepository.save(txLog);

		return response;
	}

}
