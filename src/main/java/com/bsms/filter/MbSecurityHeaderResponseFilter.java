package com.bsms.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsms.cons.MbApiConstant;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

public class MbSecurityHeaderResponseFilter implements ContainerResponseFilter {
	
	private static Logger log = LoggerFactory.getLogger(MbSecurityHeaderResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        log.debug("Start SecurityHeaderResponseFilter... ");

        if (requestContext.getSecurityContext() instanceof MbSecurityContext){

            MbSecurityContext securityContext = (MbSecurityContext) requestContext.getSecurityContext();
            if (securityContext.getAccessToken() != null) {
                responseContext.getHeaders().add(MbApiConstant.ACCESS_TOKEN_HEADER, securityContext.getAccessToken());
            }

        }

        /*
         * CORS Spesification
         * http://www.codingpedia.org/ama/how-to-add-cors-support-on-the-server-side-in-java-with-jersey/
         * Angular2 needs
         */

        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*"); //domain asal yg diijinkan
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "POST, GET, DELETE, OPTIONS");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization, Fingerprint");
        responseContext.getHeaders().add("Access-Control-Expose-Headers", "access_token"); //https://developer.mozilla.org/es/docs/Web/HTTP/Headers/Access-Control-Expose-Headers
    }

}
