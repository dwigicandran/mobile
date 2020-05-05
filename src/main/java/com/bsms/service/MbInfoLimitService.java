package com.bsms.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.Bank;
import com.bsms.restobjclient.BankDispResp;
import com.bsms.restobjclient.ContentInfoLimit;
import com.bsms.restobjclient.ContentInqTrf;
import com.bsms.restobjclient.ContentIntTrf;
import com.bsms.restobjclient.InfoLimitDispResp;
import com.bsms.restobjclient.InquiryTrfDispResp;
import com.bsms.restobjclient.InquiryTrfReq;
import com.bsms.restobjclient.InquiryTrfResp;
import com.bsms.restobjclient.InternalTrfDispResp;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("infoLimit")
public class MbInfoLimitService extends MbBaseServiceImpl implements MbService  {
	@Value("${sql.conf}")
    private String sqlconf;
	
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
	
    private static Logger log = LoggerFactory.getLogger(MbInfoLimitService.class);
    
	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {
		 	String result = "99";
		 
		 	ResultSet rs,rs2;
	        Statement stmt,stmt2;
	        String SQL,SQL2;

	        double trxAmtLimit = 0;
	        double dailyAmtLimit = 0;
	        double remainingLimit = 0;
	        int trxType=99;
	        String trxDesc=null;
	        List<ContentInfoLimit> content = new ArrayList<>();
			
			 try (Connection con = DriverManager.getConnection(connectionUrl);) 
		        {
		        	stmt= con.createStatement();
		        	SQL= "select * from mb_limit where customer_type="+request.getCustomerLimitType()+" and enabled=1";
		            rs = stmt.executeQuery(SQL);
		            
		            while(rs.next()) 
			            {
		            	 result="00";
		            	 trxType=rs.getInt("trx_type");
		            	 trxAmtLimit = rs.getDouble("trx_amount_limit");
		                 dailyAmtLimit = rs.getDouble("daily_amount_limit");
		                
		                 switch(trxType)
		                 {
		                 case 0:
		                	 trxDesc="Transfer BSM";
		                	 break;
		                 case 1:
		                	 trxDesc="Purchase";
		                	 break;
		                 case 2:
		                	 trxDesc="Payment";
		                	 break;
		                 case 3:
		                	 trxDesc="Transfer Non-BSM";
		                	 break;
		                 case 4:
		                	 trxDesc="Transfer SKN";
		                	 break;
		                 case 7:
		                	 trxDesc="Cashless Withdrawal";
		                	 break;
		                 case 8:
		                	 trxDesc="Top Up E-Money";
		                	 break;
		                 }
		                 
		                   Calendar calTrxDate = Calendar.getInstance();
			               double lastAmount;
			              
			               stmt2= con.createStatement();
			               SQL2= "select * from mb_limit_tracking where msisdn='"+request.getMsisdn()+"' and trx_type="+trxType+"";
			               rs2 = stmt2.executeQuery(SQL2);

			               if (rs2.next()) {
			                   Calendar calLastTrxDate = Calendar.getInstance();
			                   calLastTrxDate.setTime((Date) rs2.getObject("last_trx_date"));
			                   if (calLastTrxDate.get(Calendar.DATE) == calTrxDate.get(Calendar.DATE) && 
			                       calLastTrxDate.get(Calendar.MONTH) == calTrxDate.get(Calendar.MONTH) &&
			                       calLastTrxDate.get(Calendar.YEAR) == calTrxDate.get(Calendar.YEAR))
			                   {
			                       lastAmount = rs2.getDouble("total_amount");
			                   }
			                   else 
			                   {
			                       lastAmount = (double) 0;
			                   }                    
			                  
			               }
			               else {
			                   lastAmount = (double) 0;
			                   
			               }
			               
			               remainingLimit=dailyAmtLimit-lastAmount;
			               
			               rs2.close();
			     	       stmt2.close();
			     	       
			     	       	LibFunctionUtil libFunct=new LibFunctionUtil();
						    String trxAmtLimit_display = libFunct.formatIDRCurrency(trxAmtLimit);
						    String dailyAmtLimit_display = libFunct.formatIDRCurrency(dailyAmtLimit);
						    String remainingLimit_display = libFunct.formatIDRCurrency(remainingLimit);

							content.add(new ContentInfoLimit(trxDesc,"Limit Per Transaksi : "+trxAmtLimit_display,
									"Limit Per Hari : "+dailyAmtLimit_display,"Sisa Limit Harian : "+remainingLimit_display));
			            }
		          
		            
		            rs.close();
		            stmt.close();
		            
		            //close connection
				    con.close();
		        	
				  
		           
		        } catch (SQLException e) {
		        	System.out.println(e.toString());
		        	result="99";		        	
		        }
	
			 
			 if(result=="00")
				 {
				 InfoLimitDispResp infoLimitDispResp = new InfoLimitDispResp(content);
				 
				 mbApiResp = MbJsonUtil.createResponseTrf("00",
	        				"Success",
	        				infoLimitDispResp,""); 
				 }
			 else
			 {
				  mbApiResp = MbJsonUtil.createResponseTrf("99",
		             		"Informasi Limit Gagal",
		     				null,""); 
			 }
           
             
		return mbApiResp;
	}

}
