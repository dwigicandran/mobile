package com.bsms.service.home;

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
import com.bsms.restobjclient.home.Banner1;
import com.bsms.restobjclient.home.Banner2;
import com.bsms.restobjclient.home.BannerDispResp;
import com.bsms.restobjclient.transfer.Bank;
import com.bsms.restobjclient.transfer.BankDispResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("listBanner")
public class GetListBanner extends MbBaseServiceImpl implements MbService{

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
	    
	    private static Logger log = LoggerFactory.getLogger(GetListBanner.class);
	    
	    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
				throws Exception {
	        
			try {
		    
		        try (Connection con = DriverManager.getConnection(connectionUrl);) 
		        {
		        	List<Banner1> banner1 = new ArrayList<>();
		        	List<Banner2> banner2 = new ArrayList<>();

		        	Statement stmt,stmt2;
		        	String SQL,SQL2;
		        	
		        	stmt= con.createStatement();
		        	SQL= "SELECT * from MB_Banner where section='1'";
		            ResultSet rs = stmt.executeQuery(SQL);
		            
		            while (rs.next()) 
	 	            {
		            	banner1.add(new Banner1(rs.getString("img")));
	 	            }
		            
		            stmt2= con.createStatement();
		        	SQL2= "SELECT * from MB_Banner where section='2'";
		            ResultSet rs2 = stmt2.executeQuery(SQL2);
		            
		            while (rs2.next()) 
	 	            {
		            	banner2.add(new Banner2(rs2.getString("img")));
	 	            }
		            
		            
		 	        con.close();
		        	
		 	       BannerDispResp bannerDispResp = new BannerDispResp(banner1,banner2);
		           mbApiResp = MbJsonUtil.createResponseBank("00","Success",bannerDispResp);
		            
		           
		        } catch (SQLException e) {
		        	mbApiResp = MbJsonUtil.createResponseBank("99","ListBanner(), Db Connection Error",null);
		        	MbLogUtil.writeLogError(log, "ListBanner(), Db Connection Error", MbApiConstant.NOT_AVAILABLE);
		        	MbLogUtil.writeLogError(log, e, e.toString());
		        	
		        }

			} catch (Exception e) {
				mbApiResp = MbJsonUtil.createResponseBank("99","ListBanner(), System Error",null);
				MbLogUtil.writeLogError(log, "ListBanner(), Error System", MbApiConstant.NOT_AVAILABLE);
	        	MbLogUtil.writeLogError(log, e, e.toString());
			}
	       
			return mbApiResp;
		}

	    
}

