package com.bsms.util;

import org.mindrot.jbcrypt.BCrypt;

public class MbPasswordUtil {

	public static String hashPassword(String plainTextPassword){
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    public static boolean isValid(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
	
}
