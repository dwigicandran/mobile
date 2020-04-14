package com.bsms.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbAppContent;
import com.bsms.domain.Security;
import com.bsms.repository.CardmappingRepository;
import com.bsms.repository.CustomerRepository;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.SecurityRepository;
import com.bsms.restobj.BalanceInfoResp;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobj.MbApiStatusResp;
import com.bsms.restobjclient.ListAccountDispResp;
import com.bsms.restobjclient.ListAccountReq;
import com.bsms.restobjclient.ListAccountResp;
import com.bsms.restobjclient.OnlineStatementResp;
import com.bsms.restobjclient.PortofolioResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxIdUtil;
import com.dto.accountlist.Allacc;
import com.dto.accountlist.FinTrxAccount;
import com.dto.accountlist.ListOfAccount;
import com.dto.accountlist.Specfintrxacc;
import com.dto.portofolio.ListOfPortofolio;
import com.google.gson.Gson;

@Service("listAccount")
public class MbListAccountServiceImpl extends MbBaseServiceImpl implements MbService {

	@Autowired
	CardmappingRepository cardMappingRepository;

	@Autowired
	SecurityRepository securityRepository;

	@Autowired
	CardmappingRepository cardmappingRepository;

	@Autowired
	MbAppContentRepository mbAppContentRepository;

	@Autowired
	private MbTxLogRepository txLogRepository;

	@Value("${core.service.listaccount}")
	private String accountlistUrl;

	@Value("${core.service.portofolio}")
	private String portofolioUrl;

	@Value("${param.fintrx}")
	private String fintrx;

	@Value("${param.fintrxmenuid}")
	private String menulist;

	@Value("${param.specfintrx}")
	private String specmenulist;

	@Value("${core.uid}")
	private String coreUid;

	@Value("${core.pass}")
	private String corePass;

	@Value("${core.company}")
	private String coreCompany;

	@Value("${core.columnname}")
	private String coreColumnname;

	@Value("${core.colomName2}")
	private String colomNamePortofolio;

	@Value("${core.operand}")
	private String coreOperand;

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {

		MbApiTxLog txLog = new MbApiTxLog();
		txLogRepository.save(txLog);
		
		String responseDesc = null;
		String responseCode = null;

		MbApiResp mbApiResp = null;

		Security security = securityRepository.findByMbSessionId(request.getSessionId());
		Long customerId = security.getCustomerId();

		List<CardMapping> cardMapping = cardmappingRepository.findAccountnumberByCustomerid(customerId);
		List<String> accNum = new ArrayList<String>();

		for (CardMapping cm : cardMapping) {
			accNum.add(cm.getAccountnumber());
		}

		ListAccountReq listAccountReq = new ListAccountReq();

		listAccountReq.setTransactionId(TrxIdUtil.getTransactionID(6));
		listAccountReq.setCoreUsername(coreUid);
		listAccountReq.setCorePassword(corePass);
		listAccountReq.setCoreCompany(coreCompany);
		listAccountReq.setColomName(coreColumnname);
		listAccountReq.setOperand(coreOperand);
		listAccountReq.setListAccountNumber(accNum);

		System.out.println(new Gson().toJson(listAccountReq));

		try {

			HttpEntity<?> req = new HttpEntity(listAccountReq, RestUtil.getHeaders());

			RestTemplate restTemps = new RestTemplate();

			String url = accountlistUrl;
			ResponseEntity<ListAccountResp> response = restTemps.exchange(url, HttpMethod.POST, req,
					ListAccountResp.class);
			ListAccountResp listAccountResp = response.getBody();

			if ("00".equals(listAccountResp.getResponseCode())) {

				ListAccountDispResp listAccountDispResp = new ListAccountDispResp();
				listAccountDispResp.setCustdisp(listAccountResp.getContent().getCustdisp());
				listAccountDispResp.setMenulist(menulist);
				listAccountDispResp.setSpecmenulist(specmenulist);

				List<ListOfAccount> list = listAccountResp.getContent().getListOfAccount();

				String custIdFromLoa = null;
				for (int i = 0; i < 1; i++) {
					custIdFromLoa = list.get(i).getCustomer();
				}

				// get spesific account
				ListAccountReq ListAccountPortofolioReq = new ListAccountReq();

				ListAccountPortofolioReq.setCoreUsername(coreUid);
				ListAccountPortofolioReq.setCorePassword(corePass);
				ListAccountPortofolioReq.setCoreCompany(coreCompany);
				ListAccountPortofolioReq.setColomName(colomNamePortofolio);
				ListAccountPortofolioReq.setOperand(coreOperand);
				ListAccountPortofolioReq.setAccountNumber(custIdFromLoa); // customer dari listOfAccount

				System.out.println(new Gson().toJson("::: portofolio ::: " + ListAccountPortofolioReq));
				List<Specfintrxacc> specfintrxacclist = new ArrayList<Specfintrxacc>();
				List<FinTrxAccount> fintrxaccountlist = new ArrayList<FinTrxAccount>();
				List<Allacc> allacclist = new ArrayList<Allacc>();

				FinTrxAccount finTrxAccount;
				Allacc allaccount;
				Specfintrxacc specfintrxaccount;

				try {

					HttpEntity<?> reqs = new HttpEntity(ListAccountPortofolioReq, RestUtil.getHeaders());

					RestTemplate restTemps2 = new RestTemplate();

					String urlPortofolio = portofolioUrl;
					ResponseEntity<PortofolioResp> responsep = restTemps.exchange(urlPortofolio, HttpMethod.POST, reqs,
							PortofolioResp.class);
					PortofolioResp portofolioResp = responsep.getBody();
					System.out.println(new Gson().toJson(responsep.getBody()));

					List<ListOfPortofolio> listPortofolio = portofolioResp.getContent().getListOfPortofolio();
					for (ListOfPortofolio lp : listPortofolio) {
						specfintrxaccount = new Specfintrxacc();
						allaccount = new Allacc();
						if ("6012".equals(lp.getCategory())) { // TODO : update menggunakan value dari application
																// properties
							specfintrxaccount.setId_account(lp.getAccountNumber());
							specfintrxaccount.setName(lp.getAccountNumber().concat(" - " + lp.getCategoryDescription())
									.concat(" - (" + lp.getCurrency() + ")"));
							specfintrxacclist.add(specfintrxaccount);
							
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

				for (ListOfAccount la : list) {

					allaccount = new Allacc();
					finTrxAccount = new FinTrxAccount();
					specfintrxaccount = new Specfintrxacc();

					allaccount.setId_account(la.getAccountNumber());
					allaccount.setName(la.getAccountNumber().concat(" - " + la.getAccountType())
							.concat(" - (" + la.getCurrency() + ")"));
					allacclist.add(allaccount);

					if ("6010".equals(la.getCategory()) || "6001".equals(la.getCategory())
							|| "6003".equals(la.getCategory()) || "6007".equals(la.getCategory())
							|| "6017".equals(la.getCategory()) || "1001".equals(la.getCategory())) {
						// TODO: pindahkan ke application.properties
						finTrxAccount.setName(la.getAccountNumber().concat(" - " + la.getAccountType())
								.concat(" - (" + la.getCurrency() + ")"));
						finTrxAccount.setId_account(la.getAccountNumber());
						fintrxaccountlist.add(finTrxAccount);

						specfintrxaccount.setId_account(la.getAccountNumber());
						specfintrxaccount.setName(la.getAccountNumber().concat(" - " + la.getAccountType())
								.concat(" - (" + la.getCurrency() + ")"));
						specfintrxacclist.add(specfintrxaccount);
					}
				}

				listAccountDispResp.setFinTrxAccount(fintrxaccountlist);
				listAccountDispResp.setSpecfintrxacc(specfintrxacclist);
				listAccountDispResp.setAllacc(allacclist);

				mbApiResp = MbJsonUtil.createResponse(request, listAccountDispResp,
						new MbApiStatusResp(listAccountDispResp.getResponseCode(), MbApiConstant.OK_MESSAGE),
						listAccountResp.getResponseCode(), MbApiConstant.SUCCESS_MSG);

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

		return mbApiResp;
	}

}
