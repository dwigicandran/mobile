package com.bsms.service.favorite;

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
import com.bsms.restobjclient.favorite.Favorit;
import com.bsms.restobjclient.favorite.FavoritDisp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("listFavorit")
public class GetListFavorite extends MbBaseServiceImpl implements MbService{

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
	    
	    private static Logger log = LoggerFactory.getLogger(GetListFavorite.class);
	    
	    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
				throws Exception {
	        
			try {
		    
		        try (Connection con = DriverManager.getConnection(connectionUrl);) 
		        {
		        	List<Favorit> favorit = new ArrayList<>();

		        	Statement stmt;
		        	String SQL;
		        	
		        	stmt= con.createStatement();
		        	SQL= "SELECT * from Favorite where submodul_id='"+request.getSub_modul_id()+"' and msisdn='"+request.getMsisdn()+"'";
		            ResultSet rs = stmt.executeQuery(SQL);
		            
		            while (rs.next()) 
	 	            {
		            	if(request.getSub_modul_id().equalsIgnoreCase("TR01") || request.getSub_modul_id().equalsIgnoreCase("TR02"))
						{
							favorit.add(new Favorit(rs.getString("id_fav"),rs.getString("fav_title")+";"+rs.getString("destinationAccountName")+
									" - "+rs.getString("bankName")+" - "+rs.getString("destinationAccountNumber")));
						}
		            	else if(request.getSub_modul_id().equalsIgnoreCase("PU02"))
		            	{
		            		favorit.add(new Favorit(rs.getString("billkey1")+";"+rs.getString("billerid"),
		            				rs.getString("fav_title")+";"+rs.getString("billkey1")));
		            	}
		            	else if(request.getSub_modul_id().equalsIgnoreCase("PU05"))
		            	{
		            		favorit.add(new Favorit(rs.getString("billkey1")+";"+rs.getString("billerid"),
		            				rs.getString("fav_title")+";"+rs.getString("billkey1")));
		            	}
		            	else if(request.getSub_modul_id().substring(0, 2).equalsIgnoreCase("PU"))
		            	{
		            		favorit.add(new Favorit(rs.getString("billkey1"),
		            				rs.getString("fav_title")+";"+rs.getString("billkey1")));
		            	}
		            	else if(request.getSub_modul_id().substring(0, 2).equalsIgnoreCase("PY"))
		            	{
		            		favorit.add(new Favorit(rs.getString("billkey1")+";"+rs.getString("billerid"),
		            				rs.getString("fav_title")+";"+rs.getString("billkey1")));
		            	}
		            	
		            	
		            	
	 	            }
		            rs.close();
		            stmt.close();
		 	        con.close();
		        	
		 	       FavoritDisp favoritDisp = new FavoritDisp(favorit);
		           mbApiResp = MbJsonUtil.createResponseBank("00","Success",favoritDisp);
		            
		           
		        } catch (SQLException e) {
		        	mbApiResp = MbJsonUtil.createResponseBank("99","List_Favorite(), Db Connection Error",null);
		        	MbLogUtil.writeLogError(log, "List_Favorite(), Db Connection Error", MbApiConstant.NOT_AVAILABLE);
		        	MbLogUtil.writeLogError(log, e, e.toString());
		        	
		        }

			} catch (Exception e) {
				mbApiResp = MbJsonUtil.createResponseBank("99","List_Favorite(), System Error",null);
				MbLogUtil.writeLogError(log, "List_Favorite(), Error System", MbApiConstant.NOT_AVAILABLE);
	        	MbLogUtil.writeLogError(log, e, e.toString());
			}
	       
			return mbApiResp;
		}

	    
}

