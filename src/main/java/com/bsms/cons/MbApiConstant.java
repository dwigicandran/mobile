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
    
    public static final String DATE_FORMAT_TRX = "yyyyMMddHHmmss";
	
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
    
    public static final String STATUS_FAILED = "FAIL";
    
    public static final String DEFAULT_LANG = "id";

    public static char[] CHAR_FOR_RANDOMIZE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$^&*+?/".toCharArray();

    public static Integer INSERT_LT = 0;
    public static Integer UPDATE_LT = 1;

    public static Integer TRANSFER = 0;
    public static Integer TRANSFER_ONLINE = 3;
    public static Integer TRANSFER_SKN = 4;
    public static Integer TRANSFER_RTGS = 5;
    public static Integer TRANSFER_CASH = 6;
    public static Integer CW = 7;
    public static Integer EMONEY = 8;
    public static Integer PURCHASE = 1;
    public static Integer PAYMENT = 2;
    public static Integer QRIS = 10;
    
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
