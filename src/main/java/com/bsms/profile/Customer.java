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
import com.bsms.controller.ApiBaseController;
import com.bsms.except.AppException;
import com.bsms.util.MbLogUtil;
import com.bsms.util.MessageUtil;
import com.google.gson.Gson;
import com.zaxxer.hikari.HikariDataSource;

import net.sf.json.JSONObject;

@Component
public class Customer {

	private static Logger log = LoggerFactory.getLogger(Customer.class);
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public JSONObject obtain(String langId, String customerId) throws Exception {
		JSONObject response = null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
		  conn = dataSource.getConnection();
	      ps = conn.prepareStatement("SELECT Name, ISNULL(ActivationCode, '') ActivationCode, ISNULL(email, '') email, MSISDN, ISNULL(imei, '') imei, type"
                                   + " FROM Customer with (NOLOCK)"
                                   + " where id=?");
	      int i = 0;
	      ps.setObject(++i, customerId);
	      rs = ps.executeQuery();
	      if (rs.next()) {
        	response = new JSONObject();
        	response.put("msisdn", rs.getString("msisdn"));
        	response.put("name", rs.getString("name"));
        	response.put("email", rs.getString("email"));
        	response.put("type", rs.getString("type"));
        	
    	  	log.info("Customer: " + response.toString());
	      }
	      else {
	    	MbLogUtil.writeLogError(log, new Exception("Customer ID: ".concat(customerId).concat(" can not be found.")), MbApiConstant.NOT_AVAILABLE);
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
		
		return response;
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

	}

}
