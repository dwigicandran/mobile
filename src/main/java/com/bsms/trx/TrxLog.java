package com.bsms.trx;

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
public class TrxLog {

	private static Logger log = LoggerFactory.getLogger(TrxLog.class);
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public JSONObject obtain(String langId, String trxId) throws Exception {
		JSONObject response = null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
		  conn = dataSource.getConnection();
	      ps = conn.prepareStatement("SELECT CAST(request AS TEXT) AS request, CAST(response AS TEXT) AS response"
	              + " FROM MB_LogOut with (NOLOCK) where transaction_id=? AND status='1'");
	      int i = 0;
	      ps.setObject(++i, trxId);
	      rs = ps.executeQuery();
	      if (rs.next()) {
        	response = new JSONObject();
        	response.put("request", rs.getString("request"));
        	response.put("response", rs.getString("response"));
        	
    	  	log.info("Trx. Log.: " + trxId + "-" + response.toString());
	      }
	      else {
	    	MbLogUtil.writeLogError(log, new Exception("Trx ID: ".concat(trxId).concat(" can not be found.")), MbApiConstant.NOT_AVAILABLE);
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
