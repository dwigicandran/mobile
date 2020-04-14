package com.bsms.util;

public class GenACUtil {

	public String genAC(String key) {
		
		int keyLength = key.length();
		byte[] arrKey = key.getBytes();
		byte[] arrKey1 = new byte[keyLength];
		for (int i = 0; i < keyLength; i++)
			arrKey1[keyLength - (i + 1)] = (byte) (arrKey[i] ^ 0b00110011);
		return new String(arrKey1).hashCode() + "";
		
	}
}
