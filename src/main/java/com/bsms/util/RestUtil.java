package com.bsms.util;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

public class RestUtil {

private static MultiValueMap<String, String> headers; 
	
	public static MultiValueMap<String, String> getHeaders(){
		if (headers == null){
			headers = new LinkedMultiValueMap<String, String>();
			Map<String, String> map = new HashMap<String, String>();
			map.put("Content-Type", "application/json");
			headers.setAll(map);
		}
		return headers;
	}
	
}
