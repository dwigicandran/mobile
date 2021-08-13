package com.bsms.verification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.bsms.cons.MbApiConstant;
import com.bsms.except.AppException;
import com.bsms.profile.CardMapping;
import com.bsms.util.MbLogUtil;
import com.bsms.util.MessageUtil;

import net.sf.json.JSONObject;

@Component
public class PINVerification {

	private static Logger log = LoggerFactory.getLogger(PINVerification.class);
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void execute(String langId, String customerId) throws Exception {
		JSONObject response = null;
		
		int failedPINCount = -1;
	    boolean mustUpdate = false;
	    
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
		  conn = dataSource.getConnection();
	      ps = conn.prepareStatement("SELECT failedPINCount from customer with (NOLOCK) "
                  + "where id=?");
	      int i = 0;
	      ps.setObject(++i, customerId);
	      rs = ps.executeQuery();
	      if (rs.next()) {
	    	failedPINCount = rs.getInt("failedPINCount");
	    	//MbLogUtil.writeLogError(log, new Exception("Transaction: ".concat(customerId + "-" + dateLocal).concat(" already exists.")), MbApiConstant.NOT_AVAILABLE);
	    	//throw new AppException(AppException.DEFAULT_RC, MessageUtil.obtain("600002", langId));
	      }
	      /*else {
	    	MbLogUtil.writeLogMsg(log, "info", "Transaction: ".concat(customerId + "-" + dateLocal).concat(" doesn't exists."), MbApiConstant.NOT_AVAILABLE);
	      }*/
	      
	      if (failedPINCount >= 0) {
	          if (failedPINCount < 3 ) {
	        	  // 
		          /*json_request.put("DE0", "verifypin");
		          json_request.put("ZPK", zpk);
		          json_request.put("DE2", pan);
		          json_request.put("DE18", delivery_channel);
		          json_request.put("DE37", transaction_id);
		          json_request.put("DE42", msisdn);
		          json_request.put("DE43", customer_name);
		          json_request.put("DE52", pin);
		          json_request.put("PINOFFSET", pinOffset);
	
	              response_code = ParseJsonIso("DE39");
	              if (response_code.equals("00")) {           
	                  isPINValid = true;
	                  mustUpdate = (failedPINCount != 0);
	                  failedPINCount = 0;
	              }
	              else {
	                if ("01".equals(response_code)) {
	                    mustUpdate = true;
	                    failedPINCount++;
	                    if (failedPINCount < 3)
	                      response_code = "55";
	                    else
	                      response_code = "38";
	                }
	                else {
	                    response_code = "05";
	                }
	                response = ErrorMessage();
	              }*/
	              // Update failedPINCount
	              if (mustUpdate) {
	            	  rs.close();
	            	  rs = null;
	            	  ps.close();
	            	  
	                  ps = conn.prepareStatement("update customer set failedPINCount=?"
	                                           + " where id=?");
	                  i = 0;
	        	      ps.setObject(++i, failedPINCount);
	        	      ps.setObject(++i, customerId);
	                  /*String sql = "update customer set failedPINCount=" + failedPINCount + " "
	                               + "where msisdn='" + msisdn + "'";*/
	                  if (ps.executeUpdate() != 1) {
	                	  MbLogUtil.writeLogError(log, new Exception("PIN Verification: ".concat(customerId).concat(", failedPINCount update failed.")), MbApiConstant.NOT_AVAILABLE);
	      		    	  throw new AppException(AppException.DEFAULT_RC, MessageUtil.obtain("600002", langId));
	                  }
	              }
	          }
	          else {
	              /*response_code = "38";
	              response = ErrorMessage();*/
	          }
	        }
	        else {
	            //response = ErrorMessage();
	        	MbLogUtil.writeLogError(log, new Exception("PIN Verification: ".concat(customerId).concat(", failed PIN Count invalid.")), MbApiConstant.NOT_AVAILABLE);
		    	throw new AppException(AppException.DEFAULT_RC, MessageUtil.obtain("600002", langId));
	        }
	    } 
		catch (AppException e) {
			//e.printStackTrace();
			MbLogUtil.writeLogError(log, e, MbApiConstant.NOT_AVAILABLE);
			
			throw e;
		}
	    catch (Exception e) {
	    	//e.printStackTrace();
	    	MbLogUtil.writeLogError(log, e, MbApiConstant.NOT_AVAILABLE);
	    		    	
	    	throw new Exception(MessageUtil.obtain("600002", langId));
	    }
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		
		//return response;
	}
	
}
