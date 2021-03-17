//package com.bsms.service.favorite;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.container.ContainerRequestContext;
//import javax.ws.rs.core.HttpHeaders;
//
//import org.json.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.MessageSource;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import com.bsms.cons.MbApiConstant;
//import com.bsms.domain.MbApiTxLog;
//import com.bsms.repository.MbTxLogRepository;
//import com.bsms.restobj.MbApiReq;
//import com.bsms.restobj.MbApiResp;
//import com.bsms.restobjclient.favorite.Favorit;
//import com.bsms.restobjclient.transfer.ContentInqTrf;
//import com.bsms.restobjclient.transfer.InquiryTrfDispResp;
//import com.bsms.restobjclient.transfer.InquiryTrfReq;
//import com.bsms.restobjclient.transfer.InquiryTrfResp;
//import com.bsms.service.base.MbBaseServiceImpl;
//import com.bsms.service.base.MbService;
//import com.bsms.util.MbJsonUtil;
//import com.bsms.util.RestUtil;
//import com.bsms.util.LibFunctionUtil;
//import com.bsms.util.MbLogUtil;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//
////addition by Dodo
//import com.bsms.util.TrxIdUtil;
//import com.bsms.util.TrxLimit;
//
//@Service("editFavorite")
//public class EditFavorite extends MbBaseServiceImpl implements MbService  {
//	@Value("${sql.conf}")
//	private String connectionUrl;
//
//	@Value("${core.service.inquiryTransfer}")
//    private String inquiryTransfer;
//
//	@Value("${core.service.inquiryOnlineTransfer}")
//    private String inquiryOnlineTransfer;
//
//	@Autowired
//    private ObjectMapper objMapper;
//
//    @Autowired
//    private MessageSource msg;
//
//    @Autowired
//    private MbTxLogRepository txLogRepository;
//
//    RestTemplate restTemplate = new RestTemplate();
//
//    MbApiResp mbApiResp;
//
//    Client client = ClientBuilder.newClient();
//
//    private static Logger log = LoggerFactory.getLogger(EditFavorite.class);
//
//	@Override
//	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
//			throws Exception {
//		LibFunctionUtil libFunct=new LibFunctionUtil();
//		String trx_id=libFunct.getTransactionID(6);
//
//		MbApiTxLog txLog = new MbApiTxLog();
//        txLogRepository.save(txLog);
//
//		//========== edit favorite ==============//
//		try (Connection con = DriverManager.getConnection(connectionUrl);)
//        {
//        	Statement stmt;
//        	String SQL=null;
//
//			stmt= con.createStatement();
//			SQL= "Update Favorite set fav_title='"+request.getFav_title()+"',created=GETDATE() where id_fav='"+request.getId_favorit()+"' and "
//					+ "msisdn='"+request.getMsisdn()+"'";
//			stmt.executeUpdate(SQL);
//
//                mbApiResp = MbJsonUtil.createResponseTrf("00",
//    					"Edit Favorite Success",
//       				null,"");
//
//            con.close();
//
//
//        } catch (SQLException e) {
//
//        	MbLogUtil.writeLogError(log, e, e.toString());
//
//        	mbApiResp = MbJsonUtil.createResponseTrf("99",
//					"Edit Favorite Failed",
//    				null,"");
//
//        }
//
//        txLog.setResponse(mbApiResp);
//		txLogRepository.save(txLog);
//
//		return mbApiResp;
//	}
//
//}
