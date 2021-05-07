package com.bsms.service.cardotp.odc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bsms.cons.MbConstant;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.cardotp.ListOfNumTrx;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;

import io.jsonwebtoken.lang.Strings;

@Service("getOdcNumTrx")
public class GetOdcNumOfTrx extends MbBaseServiceImpl implements MbService {

	@Value("${odc.numOfTrx}")
    private String numOfTrx;
	
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		MbApiResp mbApiResp;
		
		String lang;
		if ("id".equals(request.getLanguage())) {
			lang = "kali pakai";
		} else {
			lang = "times";
		}
		
		String str[] = numOfTrx.split(",");
        List<String> al = new ArrayList<String>();
        al = Arrays.asList(str);
        
        StringBuffer sb = new StringBuffer();
        
        for(String s : al) {
            sb.append(s);
            sb.append(" "+lang+ ",");
        }
        
        String str1 = sb.toString();
        String str2[] = str1.split(",");
        List<String> al1 = new ArrayList<String>();
        al1 = Arrays.asList(str2);
        
        ListOfNumTrx listOfNumTrx = new ListOfNumTrx();
        listOfNumTrx.setContent(al1);
        
        mbApiResp = MbJsonUtil.createResponse(request, listOfNumTrx, "00", "Successful");
		
		return mbApiResp;
	}

}
