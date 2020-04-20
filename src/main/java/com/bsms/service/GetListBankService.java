package com.bsms.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.cons.MbApiConstant;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.Bank;
import com.bsms.restobjclient.BankDispResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("listBank")
public class GetListBankService extends MbBaseServiceImpl implements MbService{

	 	@Value("${sql.conf}")
		private String connectionUrl;
		
		@Autowired
	    private ObjectMapper objMapper;

	    @Autowired
	    private MessageSource msg;
	    
	    @Autowired
	    private MbTxLogRepository txLogRepository;
	    
	    RestTemplate restTemplate = new RestTemplate();
	    
	    MbApiResp mbApiResp;

	    Client client = ClientBuilder.newClient();
	    
	    private static Logger log = LoggerFactory.getLogger(GetListBankService.class);
	    
	    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
				throws Exception {
	        
			try {
		    
		        try (Connection con = DriverManager.getConnection(connectionUrl);) 
		        {
		        	List<Bank> bank = new ArrayList<>();

		        	Statement stmt;
		        	String SQL;
		        	
		        	stmt= con.createStatement();
		        	SQL= "SELECT Code, Jenis, Name FROM Banks with (NOLOCK) INNER JOIN "
		        			+ "BankPrior ON Code = IdBank ORDER BY PriorSort";
		            ResultSet rs = stmt.executeQuery(SQL);
		            
		            while (rs.next()) 
	 	            {
		            	bank.add(new Bank(rs.getString("Code"),rs.getString("Name")));
	 	            }
		            rs.close();
		            stmt.close();
		 	        con.close();
		        	
		 	       BankDispResp bankDispResp = new BankDispResp(bank);
		           mbApiResp = MbJsonUtil.createResponseBank("00","Success",bankDispResp);
		            
		           
		        } catch (SQLException e) {
		        	mbApiResp = MbJsonUtil.createResponseBank("99","List_Bank(), Db Connection Error",null);
		        	MbLogUtil.writeLogError(log, "List_Bank(), Db Connection Error", MbApiConstant.NOT_AVAILABLE);
		        	MbLogUtil.writeLogError(log, e, e.toString());
		        	
		        }

			} catch (Exception e) {
				mbApiResp = MbJsonUtil.createResponseBank("99","List_Bank(), System Error",null);
				MbLogUtil.writeLogError(log, "List_Bank(), Error System", MbApiConstant.NOT_AVAILABLE);
	        	MbLogUtil.writeLogError(log, e, e.toString());
			}
	       
			return mbApiResp;
		}

	    
}

