package com.bsms.service.favorite.enchance;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.Favorite;
import com.bsms.domain.MbInstitutionAcademic;
import com.bsms.domain.MbMerchant;
import com.bsms.domain.SpMerchant;
import com.bsms.repository.FavoriteRepository;
import com.bsms.repository.MbInstitutionAcademicRepository;
import com.bsms.repository.MbMerchantRepository;
import com.bsms.repository.SpMerchantRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.favorite.FavoritDispManagement;
import com.bsms.restobjclient.favorite.FavoritManagement;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.util.ArrayList;
import java.util.List;

@Service("getFavorite")
@Slf4j
public class GetFavoriteManagementEnh extends MbBaseServiceImpl implements MbService {

    @Autowired
    private SpMerchantRepository spMerchantRepository;

    @Autowired
    private MbMerchantRepository mbMerchantRepository;

    @Autowired
    private MbInstitutionAcademicRepository mbInstitutionAcademicRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    MbApiResp mbApiResp;


    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request) throws Exception {

        String billName = "";
        try {
            List<Favorite> favoriteList = favoriteRepository.findBySubmodul_idLikeAndMsisdn(request.getSection_id() + "%", request.getMsisdn());

            List<FavoritManagement> favoritManagement = new ArrayList<>();
            for (Favorite data : favoriteList) {
                billName = getBillName(data.getBillerid());
                if (request.getSection_id().equalsIgnoreCase("TR")) {
                    favoritManagement.add(new FavoritManagement(data.getId_fav(), null, data.getFav_title() + ";" + data.getDestinationAccountName() +
                            " - " + data.getBankName() + " - " + data.getDestinantionAccountNumber(), data.getSubmodul_id()));
                } else if (request.getSection_id().equalsIgnoreCase("PU")) {
                    if (data.getSubmodul_id().equalsIgnoreCase("PU02")) {
                        favoritManagement.add(new FavoritManagement(data.getId_fav(), data.getBillkey1() + ";" + data.getBillerid(),
                                data.getFav_title() + ";" + billName + data.getBillkey1(), data.getSubmodul_id()));
                    } else {
                        favoritManagement.add(new FavoritManagement(data.getId_fav(), data.getBillkey1(),
                                data.getFav_title() + ";" + billName + data.getBillkey1(), data.getSubmodul_id()));
                    }
                } else if (request.getSection_id().equalsIgnoreCase("PY")) {
                    if (data.getSubmodul_id().equalsIgnoreCase("PY13") || data.getSubmodul_id().equalsIgnoreCase("PY07")) {
                        billName = getInstitutionAcademicBillerName(data.getBillerid()) + " - ";
                        favoritManagement.add(new FavoritManagement(data.getId_fav(), data.getBillkey1() + ";" + data.getBillerid(), data.getFav_title() + ";" + billName + data.getBillkey1(), data.getSubmodul_id()));
                    } else {
                        favoritManagement.add(new FavoritManagement(data.getId_fav(), data.getBillkey1() + ";" + data.getBillerid(),
                                data.getFav_title() + ";" + billName + data.getBillkey1(), data.getSubmodul_id()));
                    }
                }
            }

            FavoritDispManagement favoritDisp = new FavoritDispManagement(favoritManagement);
            mbApiResp = MbJsonUtil.createResponseBank("00", "Success", favoritDisp);
        } catch (Exception e) {
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
        System.out.println("Get BillName :" + billerId);

        if (billerId != null) {
            try {
                if (billerId.equalsIgnoreCase("200194")) {
                    //khusus untuk linkaja, karena id nya tidak unique
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
        } else {
            billName = "";
        }

        return billName;
    }

    private String getInstitutionAcademicBillerName(String billerId) {
        //for institution and akademik billname
        String billName = "";

        try {
            MbInstitutionAcademic mbInstitutionAcademic = mbInstitutionAcademicRepository.findByPrefix(billerId);
            billName = mbInstitutionAcademic.getName().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return billName;
    }


}

