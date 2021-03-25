package com.bsms.service.favorite.enchance;

import com.bsms.domain.MbApiTxLog;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.sql.*;

@Service("delFavorite")
public class DeleteFavoriteEnh extends MbBaseServiceImpl implements MbService {
    @Value("${sql.conf}")
    private String connectionUrl;

//    @Value("${core.service.inquiryTransfer}")
//    private String inquiryTransfer;
//
//    @Value("${core.service.inquiryOnlineTransfer}")
//    private String inquiryOnlineTransfer;

    @Autowired
    private MbTxLogRepository txLogRepository;

    MbApiResp mbApiResp;

    private static Logger log = LoggerFactory.getLogger(DeleteFavoriteEnh.class);

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {

        MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);

        //========== delete favorite ==============//
        try (Connection con = DriverManager.getConnection(connectionUrl)) {

            String sql = "delete from Favorite where id_fav=? and msisdn=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, request.getId_favorit());
            ps.setString(2, request.getMsisdn());

            int row = ps.executeUpdate();

            log.info("Impacted deleted row : " + row);

            ps.close();

            mbApiResp = MbJsonUtil.createResponseTrf("00",
                    "Delete Favorite Success",
                    null, "");

        } catch (SQLException e) {
            MbLogUtil.writeLogError(log, e, e.toString());
            mbApiResp = MbJsonUtil.createResponseTrf("99",
                    "Delete Favorite Failed",
                    null, "");
        }

        txLog.setResponse(mbApiResp);
        txLogRepository.save(txLog);

        return mbApiResp;
    }

}
