package com.bsms.util;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

public class RestConfig {
	//@Value("${rest.timeout}")
    //private int rest_timeout;
	
	   //Override timeouts in request factory

    
	/*public ClientHttpRequestFactory getClientHttpRequestFactory() {
	    int timeout = 240000;
	    
	    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
	      = new HttpComponentsClientHttpRequestFactory();
	
	    clientHttpRequestFactory.setReadTimeout(timeout);
	    return clientHttpRequestFactory;
	}*/
}
