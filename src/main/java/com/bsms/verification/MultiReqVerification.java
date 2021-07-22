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
public class MultiReqVerification {

	private static Logger log = LoggerFactory.getLogger(MultiReqVerification.class);
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public void execute(String langId, String customerId, String dateLocal) throws Exception {
		JSONObject response = null;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
		  conn = dataSource.getConnection();
	      ps = conn.prepareStatement("SELECT 1 FROM MB_LogOut with (NOLOCK) "
                  + "where user_id =?"
                  + " AND date_local=?");
	      int i = 0;
	      ps.setObject(++i, customerId);
	      ps.setObject(++i, dateLocal);
	      rs = ps.executeQuery();
	      if (rs.next()) {
	    	MbLogUtil.writeLogError(log, new Exception("Transaction: ".concat(customerId + "-" + dateLocal).concat(" already exists.")), MbApiConstant.NOT_AVAILABLE);
	    	throw new AppException(AppException.DEFAULT_RC, MessageUtil.obtain("600002", langId));
	      }
	      else {
	    	MbLogUtil.writeLogMsg(log, "info", "Transaction: ".concat(customerId + "-" + dateLocal).concat(" doesn't exists."), MbApiConstant.NOT_AVAILABLE);
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
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
