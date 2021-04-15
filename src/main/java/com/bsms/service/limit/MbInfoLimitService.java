package com.bsms.service.limit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
import com.bsms.restobjclient.limit.ContentInfoLimit;
import com.bsms.restobjclient.limit.InfoLimitDispResp;
import com.bsms.restobjclient.transfer.Bank;
import com.bsms.restobjclient.transfer.BankDispResp;
import com.bsms.restobjclient.transfer.ContentInqTrf;
import com.bsms.restobjclient.transfer.ContentIntTrf;
import com.bsms.restobjclient.transfer.InquiryTrfDispResp;
import com.bsms.restobjclient.transfer.InquiryTrfReq;
import com.bsms.restobjclient.transfer.InquiryTrfResp;
import com.bsms.restobjclient.transfer.InternalTrfDispResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.LibFunctionUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.bsms.util.RestUtil;
import com.bsms.util.TrxLimit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service("infoLimit")
public class MbInfoLimitService extends MbBaseServiceImpl implements MbService {
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
        String reqLanguage = request.getLanguage() != null ? request.getLanguage() : "en";

        ResultSet rs, rs2, rs3;
        Statement stmt, stmt2, stmt3;
        String SQL, SQL2, SQL3;

        double trxAmtLimit = 0;
        double dailyAmtLimit = 0;
        double remainingLimit = 0;
        int trxType;
        String trxDesc = null;
        String category = null;
        List<ContentInfoLimit> content = new ArrayList<>();

        try (Connection con = DriverManager.getConnection(connectionUrl);) {
            stmt = con.createStatement();
            //SQL= "select * from mb_limit where customer_type="+request.getCustomerLimitType()+" and enabled=1";
            SQL = "select mb_limit.*,MB_Limit_Master.Name from mb_limit inner join MB_Limit_Master on MB_Limit.customer_type=MB_Limit_Master.Type "
                    + "where mb_limit.customer_type=" + request.getCustomerLimitType() + " and mb_limit.enabled=1";
            rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                result = "00";
                trxType = rs.getInt("trx_type");
                trxAmtLimit = rs.getDouble("trx_amount_limit");
                dailyAmtLimit = rs.getDouble("daily_amount_limit");
                category = rs.getString("Name");

                //convert trx type
                String trxType_new = Integer.toString(trxType) + "." + request.getLanguage() + ".trx.limit";
                System.out.println("trxType : " + trxType_new);

                SQL3 = "select * from setting where name like ?";
                PreparedStatement pr = con.prepareStatement(SQL3);
                pr.setString(1, trxType_new);
                rs3 = pr.executeQuery();

                if (rs3.next()) {
                    trxDesc = rs3.getString("value");
                    System.out.println("trxType : " + trxDesc);
                }

                pr.close();
                rs3.close();
		                 
		                 /*stmt3= con.createStatement();
		                 SQL3= "select * from setting where name like '"+trxType+"%'";
		                 System.out.println(SQL3);
		                 
				         rs3 = stmt3.executeQuery(SQL3);
				         
				         if(rs3.next())
				         {
				        	 trxDesc=rs3.getString("value");
				         }
				         
				         rs3.close();
			     	     stmt3.close();
		                 
		                 /*switch(trxType)
		                 {
		                 case "0":
		                	 trxDesc="Transfer BSM";
		                	 break;
		                 case "1":
		                	 trxDesc="Purchase";
		                	 break;
		                 case "2":
		                	 trxDesc="Payment";
		                	 break;
		                 case "3":
		                	 trxDesc="Transfer Non-BSM";
		                	 break;
		                 case "4":
		                	 trxDesc="Transfer SKN";
		                	 break;
		                 case "7":
		                	 trxDesc="Cashless Withdrawal";
		                	 break;
		                 case "8":
		                	 trxDesc="Top Up E-Money";
		                	 break;
		                 }*/


                Calendar calTrxDate = Calendar.getInstance();
                double lastAmount;

                stmt2 = con.createStatement();
                SQL2 = "select * from mb_limit_tracking where msisdn='" + request.getMsisdn() + "' and trx_type=" + trxType + "";
                rs2 = stmt2.executeQuery(SQL2);

                if (rs2.next()) {
                    Calendar calLastTrxDate = Calendar.getInstance();
                    calLastTrxDate.setTime((Date) rs2.getObject("last_trx_date"));
                    if (calLastTrxDate.get(Calendar.DATE) == calTrxDate.get(Calendar.DATE) &&
                            calLastTrxDate.get(Calendar.MONTH) == calTrxDate.get(Calendar.MONTH) &&
                            calLastTrxDate.get(Calendar.YEAR) == calTrxDate.get(Calendar.YEAR)) {
                        lastAmount = rs2.getDouble("total_amount");
                    } else {
                        lastAmount = (double) 0;
                    }

                } else {
                    lastAmount = (double) 0;

                }

                remainingLimit = dailyAmtLimit - lastAmount;

                rs2.close();
                stmt2.close();

                LibFunctionUtil libFunct = new LibFunctionUtil();
                String trxAmtLimit_display = libFunct.formatIDRCurrency(trxAmtLimit);
                String dailyAmtLimit_display = libFunct.formatIDRCurrency(dailyAmtLimit);
                String lastAmount_display = libFunct.formatIDRCurrency(lastAmount);
                String remainingLimit_display = libFunct.formatIDRCurrency(remainingLimit);

                String limitPerTransLabel = reqLanguage.equals("en") ? "Limit Per Transaction : " : "Limit Per Transaksi : ";
                String limitPerDayLabel = reqLanguage.equals("en") ? "Daily Limit : " : "Limit Per Hari : ";
                String limitUsedLabel = reqLanguage.equals("en") ? "Limit Used : " : "Limit Terpakai : ";
                String remainLimLabel = reqLanguage.equals("en") ? "Remaining Daily Limit : " : "Sisa Limit Harian : ";

//                content.add(new ContentInfoLimit(trxDesc, "Limit Per Transaksi : " + trxAmtLimit_display,
//                        "Limit Per Hari : " + dailyAmtLimit_display, "Limit Terpakai : " + lastAmount_display,
//                        "Sisa Limit Harian : " + remainingLimit_display));

                content.add(new ContentInfoLimit(trxDesc, limitPerTransLabel + trxAmtLimit_display,
                        limitPerDayLabel + dailyAmtLimit_display, limitUsedLabel + lastAmount_display,
                        remainLimLabel + remainingLimit_display));
            }

            rs.close();
            stmt.close();

            //close connection
            con.close();


        } catch (SQLException e) {
            System.out.println(e.toString());
            result = "99";
        }


        if (result == "00") {
            String limCatLabel = reqLanguage.equals("en") ? "Limit Category : " : "Kategori Limit : ";
            InfoLimitDispResp infoLimitDispResp = new InfoLimitDispResp(limCatLabel + category, content);

            mbApiResp = MbJsonUtil.createResponseTrf("00",
                    "Success",
                    infoLimitDispResp, "");
        } else {

            String message = reqLanguage.equals("en") ? "Limit Information Failed" : "Informasi Limit Gagal";
            mbApiResp = MbJsonUtil.createResponseTrf("99",
                    message,
                    null, "");
        }


        return mbApiResp;
    }

}
