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
import com.bsms.restobjclient.TrfMethod;
import com.bsms.restobjclient.TrfMethodDispResp;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("listTrfMethod")
public class GetListTrfMethodService extends MbBaseServiceImpl implements MbService{

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
	    
	    private static Logger log = LoggerFactory.getLogger(GetListTrfMethodService.class);
	    
	    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
				throws Exception {
	        
			try {
		    
		        try (Connection con = DriverManager.getConnection(connectionUrl);) 
		        {
		        	List<TrfMethod> trf = new ArrayList<>();

		        	Statement stmt;
		        	String SQL;
		        	
		        	stmt= con.createStatement();
		        	SQL= "SELECT * from TransferMethod";
		            ResultSet rs = stmt.executeQuery(SQL);
		            
		            while (rs.next()) 
	 	            {
		            	trf.add(new TrfMethod(rs.getString("value"),rs.getString("trf_method")));
	 	            }
		            rs.close();
		            stmt.close();
		 	        con.close();
		        	
		 	       TrfMethodDispResp trfMethodDispResp = new TrfMethodDispResp(trf);
		           mbApiResp = MbJsonUtil.createResponseTrfMethod("00","Success",trfMethodDispResp);
		            
		           
		        } catch (SQLException e) {
		        	mbApiResp = MbJsonUtil.createResponseTrfMethod("99","ListTrfMethod(), Db Connection Error",null);
		        	MbLogUtil.writeLogError(log, "ListTrfMethod(), Db Connection Error", MbApiConstant.NOT_AVAILABLE);
		        	MbLogUtil.writeLogError(log, e, e.toString());
		        	
		        }

			} catch (Exception e) {
				mbApiResp = MbJsonUtil.createResponseTrfMethod("99","ListTrfMethod(), System Error",null);
				MbLogUtil.writeLogError(log, "ListTrfMethod(), Error System", MbApiConstant.NOT_AVAILABLE);
	        	MbLogUtil.writeLogError(log, e, e.toString());
			}
	       
			return mbApiResp;
		}

	    
}

