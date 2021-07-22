package com.bsms.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.json.me.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.bsms.cons.MbApiConstant;
import com.bsms.except.AppException;
import com.bsms.util.MbLogUtil;
import com.bsms.util.MessageUtil;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;

import net.sf.json.JSONObject;

@Component
public class CustomerActivation {
	
	private static Logger log = LoggerFactory.getLogger(CustomerActivation.class);
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public JSONObject obtain(String langId, String caId) throws Exception {
		JSONObject response = null;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
		  conn = dataSource.getConnection();
	      ps = conn.prepareStatement("SELECT CustomerID, ZPK_lmk, "
	              + "CAST(mb_publicKey AS TEXT) AS mb_publickey, "
	              + "CAST(privateKey AS TEXT) AS privateKey, Status, "
	              + "mb_iccid, mb_imei "
	              + "FROM Security with (NOLOCK) where mb_session_id=?");
	      int i = 0;
	      ps.setObject(++i, caId);
	      rs = ps.executeQuery();
	      if (rs.next()) {
	          /*if (rs.getString("Status").equals("9")) {
	            //response_code = "0000";
	            //response = "Update data akan dilakukan, silahkan ulangi kembali transaksi anda.";
	          } else {*/
	            /*msisdn = getCustomerByID(getResultSet.getString("CustomerID"), "MSISDN");
	            zpk = getResultSet.getString("ZPK_lmk");
	            private_key = getResultSet.getString("privateKey");
	            ext_public_key = getResultSet.getString("mb_publickey");
	            iccid = getResultSet.getString("mb_iccid");
	            imei = getResultSet.getString("mb_imei");*/
	    	  
	        	response = new JSONObject();
	        	response.put("customerid", rs.getString("customerid"));
	        	response.put("zpk_lmk", rs.getString("ZPK_lmk"));
	        	
	    	  	log.info("Customer Activation: " + response.toString());
	          //}
	      }
	      else {
	    	  //throw new Exception("CA ID: ".concat(sessionId).concat(" can not be found."));
	    	  MbLogUtil.writeLogError(log, new Exception("CA ID: ".concat(caId).concat(" can not be found.")), MbApiConstant.NOT_AVAILABLE);
	    	  throw new AppException(AppException.DEFAULT_RC, MessageUtil.obtain("600002", langId)); //TODO : default RC 0001
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
		
		return response;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
