package com.bsms.service;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;

public interface MbService {

	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception;
	
}
