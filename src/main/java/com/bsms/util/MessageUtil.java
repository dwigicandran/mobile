package com.bsms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageUtil {
	private static DataSource dataSource;
	
	@Autowired
    public MessageUtil(DataSource dataSource) {
		MessageUtil.dataSource = dataSource;
    }
	
	/*@Autowired
    private DataSource ds;

    @PostConstruct
    public void init() {
        MessageUtil.dataSource = ds;
    }*/
	
	public static String obtain(String id, String langId) throws Exception {
		String result = "";
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
	    try {
	      conn = dataSource.getConnection();
	      ps = conn.prepareStatement("SELECT CAST(description AS TEXT) AS description "
	              + "FROM MB_AppContent with (NOLOCK) where lang_id=? "
	              + "and language=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	      int i = 0;
	      ps.setObject(++i, id);
	      ps.setObject(++i, langId);
	      rs = ps.executeQuery();
	      if (rs.next()) {
	          result = rs.getString("description").replace("[CR]", "\n");
	      }
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}

	    return result;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
