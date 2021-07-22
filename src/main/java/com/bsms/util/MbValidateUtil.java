package com.bsms.util;

public class MbValidateUtil {

	public static Boolean isValid(String...strings) {
		for(String string : strings) {
			if("".equals(string) || string == null) {
				return false;
			}
		}
		return true;
	}
	
}
