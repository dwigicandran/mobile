package com.bsms.service.cardotp.odc;

import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bsms.domain.OtpNumTrx;
import com.bsms.repository.OtpNumTrxRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.cardotp.ListOfNumTrx;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;

@Service("getOdcNumTrx")
public class GetOdcNumOfTrx extends MbBaseServiceImpl implements MbService {

	@Value("${odc.numOfTrx}")
	private String numOfTrx;
	
	@Autowired
	OtpNumTrxRepository otpNumTrxRepository;

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		MbApiResp mbApiResp;
		
		String lang = request.getLanguage();
		
		List<OtpNumTrx> otpNumTrx = otpNumTrxRepository.findAllByLang(lang);
		ListOfNumTrx listOfNumTrx = new ListOfNumTrx();
		listOfNumTrx.setContent(otpNumTrx);
        
        mbApiResp = MbJsonUtil.createResponse(request, listOfNumTrx, "00", "Successful");
		
		return mbApiResp;
	}

}
