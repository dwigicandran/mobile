package com.bsms.cons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import javax.annotation.PostConstruct;

public class MbApiConstant {
	
	private static Logger log = LoggerFactory.getLogger(MbApiConstant.class);

	public static final String TIME_FORMAT = "dd-MM-yyyy hh:mm:ss";

    public static final String DATE_FORMAT = "dd-MM-yyyy";
    
    public static final String DATE_FORMAT_TRX = "ddMMyyyy";
	
	public static final String NOT_AVAILABLE = "N/A";
	
	public static final String ERROR_NUM_HOST_HLAD= "997";
	
	public static final String HOST_HLAD = "MB";
	
	public static final String OK_MESSAGE = "Ok";
	
	public static final String SUCCESS_CODE = "00";
	
	public static final String SUCCESS_MSG = "SUCCESS";
	
	public static final String SUCCESS_MSG_INQ_BAL = "Inqury Balance Succesfull";
	
	public static final String ERR_CODE = "99";
    
	public static final String FINGERPRINT_HEADER = "fingerprint"; //req
	
    public static final String AUTHORIZATION_HEADER = "Authorization"; //req
    
    public static final String ACCESS_TOKEN_HEADER = "access_token"; //response

    public static final String JWT_USER = "user";
    
    public static final String JWT_VERIFY_TOKEN = "verifyToken";

    public static final String LOGIN_PATH = "api/services/login";
    
    public static final String LOGOUT_PATH = "api/services/logout";

    public static char[] CHAR_FOR_RANDOMIZE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$^&*+?/".toCharArray();

    @Value("${jwt.expiration.time}")
    private String jwtExpirationTime;
    @Value("${verify.token.expiration.time}")
    private String verifyTokenExpirationTime;
    @Value("${jwt.secret.key}")
    private String jwtSecretKey;
    public static long JWT_EXPIRATION_TIME = 1;
    public static long VERIFY_TOKEN_EXPIRATION_TIME = 1;
    public static String JWT_SECRET_KEY;

    @PostConstruct
    public void init() {
        log.info("  H2HRemConstant Load");
        log.info("  Load Constant from properties");

        String[] nums = StringUtils.tokenizeToStringArray(jwtExpirationTime, "*");
        for (String num : nums) { JWT_EXPIRATION_TIME *= Long.valueOf(num); }
        log.info("    JWT_EXPIRATION_TIME="+JWT_EXPIRATION_TIME);

        nums = StringUtils.tokenizeToStringArray(verifyTokenExpirationTime, "*");
        for (String num : nums) { VERIFY_TOKEN_EXPIRATION_TIME *= Long.valueOf(num); }
        log.info("    VERIFY_TOKEN_EXPIRATION_TIME="+VERIFY_TOKEN_EXPIRATION_TIME);

        JWT_SECRET_KEY=jwtSecretKey;
        log.info("    JWT_SECRET_KEY="+JWT_SECRET_KEY);
    }
	
}
