package com.bsms.util;

import org.springframework.context.MessageSource;

public class MbMsgUtil {

	private static final String ERROR_DEFAULT = "Kode Error tidak terdefinisi";
    
	public static String getMessage(String host, MessageSource msg, String errorCode){
		return getMsg(host, msg, errorCode, null, null, null);
	}
	        
	public static String getMessage(String host, MessageSource msg, String errorCode, String param1){
		return getMsg(host, msg, errorCode, param1, null, null);
	}
	        
	public static String getMessage(String host, MessageSource msg, String errorCode, String param1, String param2){
		return getMsg(host, msg, errorCode, param1, param2, null);
	}
	        
	public static String getMessage(String host, MessageSource msg, String errorCode, String param1, String param2, String param3){
		return getMsg(host, msg, errorCode, param1, param2, param3);
	}
	 
	private static String getMsg(String host, MessageSource msg, String respCode, String param1, String param2, String param3){
	                
		String errorDescription = msg.getMessage(
	                                new StringBuilder(respCode).toString(),
	                                new String[]{param1, param2, param3},
	                                ERROR_DEFAULT,
	                                null);
		StringBuilder fullMessage = new StringBuilder("[").append(host).append("] ").append(errorDescription);
		return fullMessage.toString();
	}
	
}
