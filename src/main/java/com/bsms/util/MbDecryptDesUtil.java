package com.bsms.util;

public class MbDecryptDesUtil {

	public String DecryptDes(String key, String data) {
		String result = "";
		try {
			LibDESUtil lib_des = new LibDESUtil("DES/ECB/PKCS5Padding");
			result = lib_des.Decrypt(key, data);
		} catch (Exception Ex) {
			Ex.printStackTrace();
			return "";
		}
		return result;
	}

}
