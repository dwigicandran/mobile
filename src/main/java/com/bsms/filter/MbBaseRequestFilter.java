package com.bsms.filter;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import com.bsms.util.MbErrorUtil;

public abstract class MbBaseRequestFilter implements ContainerRequestFilter {

	protected Response createUnauthorizedResponse(String errCode, String errDesc){
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(MbErrorUtil.createError(errCode, errDesc))
                .build();

    }
	
}
