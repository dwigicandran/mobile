package com.bsms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.bsms.domain.Security;
import com.bsms.repository.CardmappingRepository;
import com.bsms.repository.CustomerRepository;
import com.bsms.repository.ErrormsgRepository;
import com.bsms.repository.MbAppContentRepository;
import com.bsms.repository.SecurityRepository;
import com.bsms.restobj.MbApiResp;

@Component
public class MbVerifyPinUtil {

	@Autowired
	private static SecurityRepository securityRepository;

	public void getRC(String sessionId, String accountNumber) {

		System.out.println(sessionId + " ::: SESSION ID DALAM MB UTIL");
		
		securityRepository.findAll();
//		System.out.println(accountNumber);
//		System.out.println(customerId);
		
//		int failedPINCount = -1;
//		
//		Connection conn = null;
//		PreparedStatement ps = null;
//		ResultSet rs = null;
//
//		try {
//
//			System.out.println(customerId + " === CUSTOMER ID");
//			
//			conn = dataSource.getConnection();
//			ps = conn.prepareStatement("SELECT failedPINCount from customer with (NOLOCK) " + "where id=?");
//			int i = 0;
//			ps.setObject(++i, customerId);
//			
//			rs = ps.executeQuery();
//
//			if (rs.next()) {
//				failedPINCount = rs.getInt("failedPINCount");
//				
//				System.out.println(failedPINCount);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

	}

	public static void main(String args[]) {
//		MbVerifyPinUtil mbVerifyPinUtil = new MbVerifyPinUtil();
//		mbVerifyPinUtil.getRC("20190905110232088146", "9911198301", "74147245");
	}

}
