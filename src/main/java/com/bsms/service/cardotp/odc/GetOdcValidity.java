package com.bsms.service.cardotp.odc;

import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bsms.domain.OtpPeriod;
import com.bsms.repository.OtpPeriodRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.cardotp.ListOfPeriodValidity;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;

@Service("getOdcValidity")
public class GetOdcValidity extends MbBaseServiceImpl implements MbService {

	@Value("${odc.validityPeriod}")
	private String validityPeriod;
	
	@Autowired
	OtpPeriodRepository otpPeriodRepository;

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		MbApiResp mbApiResp;
		
		String lang = request.getLanguage();
		
		List<OtpPeriod> periodList = otpPeriodRepository.findAllByLang(lang.toLowerCase());
		ListOfPeriodValidity listOfValidity = new ListOfPeriodValidity();
		listOfValidity.setContent(periodList);

		mbApiResp = MbJsonUtil.createResponse(request, listOfValidity, "00", "Successful");

		return mbApiResp;
	}

}
