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

import com.bsms.domain.MbMerchant;
import com.bsms.domain.SpMerchant;
import com.bsms.repository.MbMerchantRepository;
import com.bsms.repository.SpMerchantRepository;
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
import com.bsms.restobjclient.favorite.FavoritDispManagement;
import com.bsms.restobjclient.favorite.FavoritManagement;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("getFavorite")
public class GetFavoriteManagement extends MbBaseServiceImpl implements MbService {

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

    private static Logger log = LoggerFactory.getLogger(GetFavoriteManagement.class);

    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {


        try (Connection con = DriverManager.getConnection(connectionUrl);) {
            List<FavoritManagement> favoritManagement = new ArrayList<>();

            Statement stmt;
            String SQL;

            stmt = con.createStatement();
            SQL = "SELECT * from Favorite where submodul_id like '" + request.getSection_id() + "%' and msisdn='" + request.getMsisdn() + "'";

            ResultSet rs = stmt.executeQuery(SQL);
            String billName;

            while (rs.next()) {

                billName = getBillName(rs.getString("billerid"));

                if (request.getSection_id().equalsIgnoreCase("TR")) {
                    favoritManagement.add(new FavoritManagement(rs.getString("id_fav"), null, rs.getString("fav_title") + ";" + rs.getString("destinationAccountName") +
                            " - " + rs.getString("bankName") + " - " + rs.getString("destinationAccountNumber"), rs.getString("submodul_id")));
                } else if (request.getSection_id().equalsIgnoreCase("PU")) {
                    if (rs.getString("submodul_id").equalsIgnoreCase("PU02")) {
                        favoritManagement.add(new FavoritManagement(rs.getString("id_fav"), rs.getString("billkey1") + ";" + rs.getString("billerid"),
                                rs.getString("fav_title") + ";" + billName + rs.getString("billkey1"), rs.getString("submodul_id")));
                    } else {
                        favoritManagement.add(new FavoritManagement(rs.getString("id_fav"), rs.getString("billkey1"),
                                rs.getString("fav_title") + ";" + billName + rs.getString("billkey1"), rs.getString("submodul_id")));
                    }

                } else if (request.getSection_id().equalsIgnoreCase("PY")) {
                    favoritManagement.add(new FavoritManagement(rs.getString("id_fav"), rs.getString("billkey1") + ";" + rs.getString("billerid"),
                            rs.getString("fav_title") + ";" + billName + rs.getString("billkey1"), rs.getString("submodul_id")));
                }

            }
            rs.close();
            stmt.close();
            con.close();

            FavoritDispManagement favoritDisp = new FavoritDispManagement(favoritManagement);
            mbApiResp = MbJsonUtil.createResponseBank("00", "Success", favoritDisp);


        } catch (SQLException e) {
            mbApiResp = MbJsonUtil.createResponseBank("99", "GetFavorite(), Db Connection Error", null);
            MbLogUtil.writeLogError(log, "List_Favorite(), Db Connection Error", MbApiConstant.NOT_AVAILABLE);
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
                //khusus untuk link aja, karena id nya tidak unique
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


}

