package com.bsms.util;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Base64;

public class LibDESUtil {

	String instance;

    public LibDESUtil(String instance) {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        this.instance = instance;
    }

//    public String Encrypt(String key, String data) throws Exception {
//        String result = "";
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(key.getBytes());
//            DESKeySpec key_spec = new DESKeySpec(md.digest());
//            SecretKeySpec DESKey = new SecretKeySpec(key_spec.getKey(), "DES");
//            Cipher cipher = Cipher.getInstance(instance);
//            cipher.init(Cipher.ENCRYPT_MODE, DESKey);
//            byte[] encrypted = cipher.doFinal(data.getBytes());
//            result = Base64.toBase64String(encrypted);
//        } catch (InvalidKeyException Ex) {
//            Ex.printStackTrace();
//            LibFunction.setLogMessage(Ex.getMessage());
//            return "";
//        } catch (NoSuchAlgorithmException Ex) {
//            Ex.printStackTrace();
//            LibFunction.setLogMessage(Ex.getMessage());
//            return "";
//        } catch (BadPaddingException Ex) {
//            Ex.printStackTrace();
//            LibFunction.setLogMessage(Ex.getMessage());
//            return "";
//        } catch (IllegalBlockSizeException Ex) {
//            Ex.printStackTrace();
//            LibFunction.setLogMessage(Ex.getMessage());
//            return "";
//        } catch (NoSuchPaddingException Ex) {
//            Ex.printStackTrace();
//            LibFunction.setLogMessage(Ex.getMessage());
//            return "";
//        }
//        return result;
//    }

    public String Decrypt(String key, String data) throws Exception {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
            DESKeySpec key_spec = new DESKeySpec(md.digest());
            SecretKeySpec DESKey = new SecretKeySpec(key_spec.getKey(), "DES");
            Cipher cipher = Cipher.getInstance(instance);
            cipher.init(Cipher.DECRYPT_MODE, DESKey);
            byte[] decrypted = cipher.doFinal(Base64.decode(data));
            result = new String(decrypted);
        } catch (InvalidKeyException Ex) {
            Ex.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException Ex) {
            Ex.printStackTrace();
            return "";
        } catch (BadPaddingException Ex) {
            Ex.printStackTrace();
            return "";
        } catch (IllegalBlockSizeException Ex) {
            Ex.printStackTrace();
            return "";
        } catch (NoSuchPaddingException Ex) {
            Ex.printStackTrace();
            return "";
        }
        return result;
    }
	
}
