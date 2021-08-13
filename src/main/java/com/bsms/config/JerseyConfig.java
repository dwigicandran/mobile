package com.bsms.config;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.bsms.controller.ApiBaseController;
import com.bsms.filter.MbLogRequestFiler;
import com.bsms.filter.MbLogResponseFilter;
import com.bsms.filter.MbSecurityHeaderResponseFilter;
import com.bsms.filter.MbTokenRequestFilter;

@Configuration
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		
		register(MbLogRequestFiler.class);
		register(MbTokenRequestFilter.class);
		register(ApiBaseController.class);
		register(MbSecurityHeaderResponseFilter.class);
		register(MbLogResponseFilter.class);
	}
	
}
