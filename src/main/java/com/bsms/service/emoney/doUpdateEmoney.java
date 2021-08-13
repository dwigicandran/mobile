package com.bsms.service.emoney;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.emoney.ContentEmoneydoPayment;
import com.bsms.restobjclient.emoney.doPaymentEmoneyResp;
import com.bsms.restobjclient.emoney.doUpdateEmoneyResp;
import com.bsms.restobjclient.limit.InfoLimitDispResp;
import com.bsms.restobjclient.transfer.Bank;
import com.bsms.restobjclient.transfer.BankDispResp;
import com.bsms.restobjclient.transfer.ContentInqTrf;
import com.bsms.restobjclient.transfer.InquiryTrfDispResp;
import com.bsms.restobjclient.transfer.InquiryTrfReq;
import com.bsms.restobjclient.transfer.InquiryTrfResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.dto.accountlist.ListOfAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("doUpdateEmoney")
public class doUpdateEmoney extends MbBaseServiceImpl implements MbService  {

	@Value("${emoney.doUpdate}")
    private String doUpdateEmoney;
	
	@Autowired
    private ObjectMapper objMapper;

    @Autowired
    private MessageSource msg;
    
    RestTemplate restTemplate = new RestTemplate();
    
    MbApiResp mbApiResp;

    ResponseEntity<InquiryTrfResp> response;
    
    Client client = ClientBuilder.newClient();
	
    private static Logger log = LoggerFactory.getLogger(doUpdateEmoney.class);
    
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {

		try {
			HttpEntity<?> req = new HttpEntity(request, RestUtil.getHeaders());
			RestTemplate restTemps = new RestTemplate();
			String url = doUpdateEmoney;

			ResponseEntity<doUpdateEmoneyResp> response = restTemps.exchange(url, HttpMethod.POST, req, doUpdateEmoneyResp.class);
			doUpdateEmoneyResp doUpdateEmoneyResp = response.getBody();
			System.out.println(new Gson().toJson(response.getBody()));

			mbApiResp = MbJsonUtil.createResponseTrf(doUpdateEmoneyResp.getResponseCode(),doUpdateEmoneyResp.getResponseMessage(),doUpdateEmoneyResp.getResponseContent(),doUpdateEmoneyResp.getTransactionId());

		} catch (Exception e) {
			mbApiResp = MbJsonUtil.createResponseTrf("99",
					e.toString(),
					null,"");
			MbLogUtil.writeLogError(log, "99", e.toString());
		}
		return mbApiResp;
	}

}
