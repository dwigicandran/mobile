package com.bsms.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MbLogResponseFilter implements ContainerResponseFilter {

    private static Logger log = LoggerFactory.getLogger(MbLogResponseFilter.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (log.isInfoEnabled()) {
            //log.info("MbLogResponseFilter called");
            StringBuilder b = new StringBuilder();
            String path = requestContext.getUriInfo().getPath();
//	        b.append("\n\nJSON Response [");
            b.append(path);
//	        b.append("] : \n");
            b.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(responseContext.getEntity()));
            if (!path.equalsIgnoreCase("api/services/checkServices")) {
                log.info(b.toString());
            }

        }
    }
}
