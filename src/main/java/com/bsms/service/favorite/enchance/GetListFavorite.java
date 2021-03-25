package com.bsms.service.favorite.enchance;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.Favorite;
import com.bsms.domain.MbMerchant;
import com.bsms.domain.SpMerchant;
import com.bsms.repository.FavoriteRepository;
import com.bsms.repository.MbMerchantRepository;
import com.bsms.repository.SpMerchantRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.favorite.Favorit;
import com.bsms.restobjclient.favorite.FavoritDisp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;
import java.util.List;

@Service("listFavorit")
public class GetListFavorite extends MbBaseServiceImpl implements MbService {

    MbApiResp mbApiResp;

    @Value("${sql.conf}")
    private String connectionUrl;

    private static Logger log = LoggerFactory.getLogger(GetListFavorite.class);

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private SpMerchantRepository spMerchantRepository;

    @Autowired
    private MbMerchantRepository mbMerchantRepository;

    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {
        List<Favorit> favorit = new ArrayList<>();
        String billName = "";
        try {
            List<Favorite> favoriteList = favoriteRepository.findAllBySubmodul_idAndMsisdn(request.getSub_modul_id(), request.getMsisdn());
            for (Favorite data : favoriteList) {
                billName = getBillName(data.getBillerid());

                if (request.getSub_modul_id().equalsIgnoreCase("TR01") || request.getSub_modul_id().equalsIgnoreCase("TR02")) {
                    favorit.add(new Favorit(data.getId_fav(), data.getFav_title() + ";" + data.getDestinationAccountName() +
                            " - " + data.getBankName() + " - " + data.getDestinantionAccountNumber()));
                } else if (request.getSub_modul_id().substring(0, 2).equalsIgnoreCase("PU")) {
                    favorit.add(new Favorit(data.getBillkey1() + ";" + data.getBillerid(),
                            data.getFav_title() + ";" + billName + data.getBillkey1()));
                } else if (request.getSub_modul_id().substring(0, 2).equalsIgnoreCase("PY")) {
                    favorit.add(new Favorit(data.getBillkey1() + ";" + data.getBillerid(),
                            data.getFav_title() + ";" + billName + data.getBillkey1()));
                }
            }
        } catch (Exception e) {
            mbApiResp = MbJsonUtil.createResponseBank("99", "List_Favorite(), System Error", null);
            MbLogUtil.writeLogError(log, "List_Favorite(), Error System", MbApiConstant.NOT_AVAILABLE);
            MbLogUtil.writeLogError(log, e, e.toString());
        }

        FavoritDisp favoritDisp = new FavoritDisp(favorit);
        mbApiResp = MbJsonUtil.createResponseBank("00", "Success", favoritDisp);

        return mbApiResp;
    }

    //add by Dwi S
    private String getBillName(String billerId) {
        //enhancemt favorite, penambahana billname di value response
        String billName;

        if(billerId != null){
            try {
                if (billerId.equalsIgnoreCase("200194")) {
                    //khusus untuk link aja
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
        }else{
            billName="";
        }

        return billName;
    }

}
