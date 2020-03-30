package com.bsms.filter;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiUser;
import com.bsms.util.MbJWTUtil;
import com.bsms.util.MbJsonUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureException;

public class MbTokenRequestFilter extends MbBaseRequestFilter {

	private static Logger log = LoggerFactory.getLogger(MbTokenRequestFilter.class);

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		log.debug("Start token filtering..");
		
		MbSecurityContext securityContext = new MbSecurityContext();
		securityContext.setIsSecure(requestContext.getSecurityContext().isSecure());
		requestContext.setSecurityContext(securityContext);	
		
		String urlPath = requestContext.getUriInfo().getPath();
		
		String authorizationHeader = requestContext.getHeaderString(MbApiConstant.AUTHORIZATION_HEADER);
		
		boolean isLogin = true;
		
		if (MbApiConstant.LOGIN_PATH.equals(urlPath)){
			log.debug("UrlPath : ["+urlPath+"] is LOGIN path.");
			isLogin = false;
		}
		
		if (MbApiConstant.LOGOUT_PATH.equals(urlPath)){
			log.debug("UrlPath : ["+urlPath+"] is LOGOUT path.");
			isLogin = false;
		}
		
		log.debug("IsLogin : "+isLogin);
		
		Claims claims = null;
		
		if (isLogin){
			
			/*if (authorizationHeader == null || "".equals(authorizationHeader)) {
				
				log.debug("Header "+MbApiConstant.AUTHORIZATION_HEADER+" is null");
				requestContext.abortWith(createUnauthorizedResponse("login.please", "Login please."));
			
			} else {
				
				authorizationHeader = authorizationHeader.substring(7, authorizationHeader.length());
				
				String username = null;
				
				try {
					claims = MbJWTUtil.getClaimsBody(authorizationHeader);
					
					log.debug("JWT signature is matched and valid.");
					
					username = claims.getSubject();
					
					long jwtExp = (long) claims.get(Claims.EXPIRATION);
					Date jwtExpDate = new Date(jwtExp);
					log.debug("   Token expiration : "+jwtExpDate);
					log.debug("   Date now : "+new Date());
					
					if (jwtExpDate.before(new Date())){
						log.debug("User '"+username+"' has expired token.");
						requestContext.abortWith(createUnauthorizedResponse("expired.token", "User has expired token."));
						
					}else{
						log.debug("User '"+username+"' has valid token.");
						Map userMap = (Map) claims.get(MbApiConstant.JWT_USER);
						
						if (userMap == null){
							
							log.debug("Invalid Token..");
							requestContext.abortWith(createUnauthorizedResponse("invalid.token", "Invalid Token.."));
						} else {
							
							MbApiUser user = new MbApiUser();
							user.setUsername((String) userMap.get("username"));
							user.setId((String) userMap.get("id"));
							user.setBatchToken((String) userMap.get("batchToken"));

							securityContext.setUserPrinsipal(user);
						}
					}
					
				} catch (SignatureException e) {
					log.debug("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.");
					requestContext.abortWith(createUnauthorizedResponse("jwt.not.valid", "JWT signature not valid."));
				}
				
			}*/		
		}
		
	}
	
	
}
