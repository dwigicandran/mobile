package com.bsms.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.glassfish.jersey.message.internal.ReaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MbLogRequestFiler implements ContainerRequestFilter {

	private static Logger log = LoggerFactory.getLogger(MbLogRequestFiler.class);
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		if (log.isInfoEnabled()){
			log.info("MbLogRequestFiler called");
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
	        InputStream in = requestContext.getEntityStream();
	        
	        StringBuilder b = new StringBuilder();
//	        b.append("\n\nJSON Request [");
	        b.append(requestContext.getUriInfo().getPath());
//	        b.append("] : \n");
	        
            ReaderWriter.writeTo(in, out);
            
            byte[] requestEntity = out.toByteArray();
            
            String jsonStr = new String(requestEntity);
            JsonNode tree = objectMapper.readTree(jsonStr);
            b.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tree));
            
            log.info(b.toString());
            requestContext.setEntityStream( new ByteArrayInputStream(requestEntity) );
            
		}
	}
	
}
