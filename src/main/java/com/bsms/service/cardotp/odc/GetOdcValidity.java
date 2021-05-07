package com.bsms.service.cardotp.odc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		MbApiResp mbApiResp;

		String lang;
		if("id".equals(request.getLanguage())) {
			lang = "jam";
		} else {
			lang = "hour";
		}

		String str[] = validityPeriod.split(",");
		List<String> al = new ArrayList<String>();
		al = Arrays.asList(str);

		StringBuffer sb = new StringBuffer();

		for (String s : al) {
			sb.append(s);
			sb.append(" " + lang + ",");
		}
		
		String str1 = sb.toString();
        String str2[] = str1.split(",");
        List<String> al1 = new ArrayList<String>();
        al1 = Arrays.asList(str2);

		ListOfPeriodValidity listOfValidity = new ListOfPeriodValidity();
		listOfValidity.setContent(al1);

		mbApiResp = MbJsonUtil.createResponse(request, listOfValidity, "00", "Successful");

		return mbApiResp;
	}

}
