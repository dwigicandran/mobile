package com.bsms.service.favorite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;

import com.bsms.domain.MbMerchant;
import com.bsms.domain.SpMerchant;
import com.bsms.repository.MbMerchantRepository;
import com.bsms.repository.SpMerchantRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
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
public class GetListFavorite extends MbBaseServiceImpl implements MbService {

    @Value("${sql.conf}")
    private String connectionUrl;

    @Autowired
    private SpMerchantRepository spMerchantRepository;

    @Autowired
    private MbMerchantRepository mbMerchantRepository;

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

            try (Connection con = DriverManager.getConnection(connectionUrl);) {
                List<Favorit> favorit = new ArrayList<>();

                Statement stmt;
                String SQL;

                stmt = con.createStatement();
                SQL = "SELECT * from Favorite where submodul_id='" + request.getSub_modul_id() + "' and msisdn='" + request.getMsisdn() + "'";
                ResultSet rs = stmt.executeQuery(SQL);

                String billName;

                while (rs.next()) {
                    billName = getBillName(rs.getString("billerid"));

                    if (request.getSub_modul_id().equalsIgnoreCase("TR01") || request.getSub_modul_id().equalsIgnoreCase("TR02")) {
                        favorit.add(new Favorit(rs.getString("id_fav"), rs.getString("fav_title") + ";" + rs.getString("destinationAccountName") +
                                " - " + rs.getString("bankName") + " - " + rs.getString("destinationAccountNumber")));
                    } else if (request.getSub_modul_id().equalsIgnoreCase("PU02")) {
                        favorit.add(new Favorit(rs.getString("billkey1") + ";" + rs.getString("billerid"),
                                rs.getString("fav_title") + ";" + billName + rs.getString("billkey1")));
                    } else if (request.getSub_modul_id().substring(0, 2).equalsIgnoreCase("PU")) {
//		            		log.info("")
                        favorit.add(new Favorit(rs.getString("billkey1"),
                                rs.getString("fav_title") + ";" + billName + rs.getString("billkey1")));
                    } else if (request.getSub_modul_id().substring(0, 2).equalsIgnoreCase("PY")) {
                        String subModulId = rs.getString("submodul_id") != null ? rs.getString("submodul_id") : "";

                        if (subModulId.equalsIgnoreCase("PY13") || subModulId.equalsIgnoreCase("PY07")) {
                            billName = getInstitutionAcademicBillerName(rs.getString("billerid"));
                        }
                        favorit.add(new Favorit(rs.getString("billkey1") + ";" + rs.getString("billerid"),
                                rs.getString("fav_title") + ";" + billName + rs.getString("billkey1")));
                    }


                }
                rs.close();
                stmt.close();
                con.close();

                FavoritDisp favoritDisp = new FavoritDisp(favorit);
                mbApiResp = MbJsonUtil.createResponseBank("00", "Success", favoritDisp);


            } catch (SQLException e) {
                mbApiResp = MbJsonUtil.createResponseBank("99", "List_Favorite(), Db Connection Error", null);
                MbLogUtil.writeLogError(log, "List_Favorite(), Db Connection Error", MbApiConstant.NOT_AVAILABLE);
                MbLogUtil.writeLogError(log, e, e.toString());

            }

        } catch (Exception e) {
            mbApiResp = MbJsonUtil.createResponseBank("99", "List_Favorite(), System Error", null);
            MbLogUtil.writeLogError(log, "List_Favorite(), Error System", MbApiConstant.NOT_AVAILABLE);
            MbLogUtil.writeLogError(log, e, e.toString());
        }

        return mbApiResp;
    }

    //add by Dwi S
    private String getBillName(String billerId) {
        //enhancemt favorite, penambahana billname di value response
        String billName;
        try {

            if (billerId.equalsIgnoreCase("200194")) {
                //khusu
                billName = "Link Aja - ";
            } else {
                SpMerchant spMerchant = spMerchantRepository.findBySpMerchantId(billerId);
                MbMerchant mbMerchant = mbMerchantRepository.findByCode(spMerchant.getMerchantCode());
                billName = mbMerchant.getFavTitle() + " - ";
            }
        } catch (Exception e) {
            e.printStackTrace();
            billName = "";
        }
        return billName;
    }

    //add by Dwi S
    private String getInstitutionAcademicBillerName(String billerId) {
        //for institution and akademik billname
        String billName = "";

        Connection con;
        try {
            con = DriverManager.getConnection(connectionUrl);
            PreparedStatement stmt = con.prepareStatement("select * from MB_InstitutionAcademic where prefix = ? ");

            stmt.setString(1, billerId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                billName = rs.getString("name") != null ? rs.getString("name").trim() + " - " : "";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return billName;
    }

}

