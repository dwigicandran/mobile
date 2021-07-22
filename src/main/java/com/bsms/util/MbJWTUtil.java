package com.bsms.util;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.HashMap;
import java.util.Map;

public class MbJWTUtil {

	private static Logger log = LoggerFactory.getLogger(MbJWTUtil.class);

    private static final String jwtSecretKey0 = "H2HR3m2o18";

    public static Claims getClaimsBody(String JWT){

        return Jwts
                .parser()
                .setSigningKey(getJwtSecretKey())
                .parseClaimsJws(JWT)
                .getBody();

    }

    public static String generateJWT(MbApiUser user, long expirationTime, String verifyToken){

        MbJWTUtil jwtUtil = new MbJWTUtil();

        MbJWTUtil.UserJwt userJwt = jwtUtil.new UserJwt(user);

        long today = System.currentTimeMillis();

        Map claims = new HashMap<>();
        claims.put(Claims.SUBJECT, userJwt.getUsername());
        claims.put(Claims.EXPIRATION, today + expirationTime);
        claims.put(Claims.ISSUED_AT, today);
        claims.put(MbApiConstant.JWT_USER, userJwt);
        claims.put(MbApiConstant.JWT_VERIFY_TOKEN, verifyToken);

        String JWT = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, getJwtSecretKey())
                .compact();

        return JWT;

    }

    private static String getJwtSecretKey(){
        StringBuilder sb = new StringBuilder();
        sb.append("19")
                .append(jwtSecretKey0)
                .append("03")
                .append(MbApiConstant.JWT_SECRET_KEY)
                .append("20")
                .append("18");

        return sb.toString();
    }

    class UserJwt implements Serializable {

        private static final long serialVersionUID = -9151808806055784581L;

        private String id;
        private String username;
        private String batchToken;
        
        public UserJwt (MbApiUser user){
            this.id = user.getId();
            this.username = user.getUsername();
            this.batchToken = user.getBatchToken();
           
        }

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getBatchToken() {
            return batchToken;
        }
        public void setBatchToken(String batchToken) {
            this.batchToken = batchToken;
        }
    }
	
}
