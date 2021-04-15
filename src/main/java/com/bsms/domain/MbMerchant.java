package com.bsms.domain;

import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//Addition By Dwi S - Februari 2021

@Entity
@Table(name = "Mb_Merchant")
@ToString
public class MbMerchant {

    @Id
    @Column(name = "Code")
    private String code;
    private int type;
    @Column(name = "id")
    private String id;
    @Column(name = "en")
    private String en;
    @Column(name = "versionname")
    private String versionName;
    @Column(name = "versionvalue")
    private String versionValue;
    @Column(name = "menu_type")
    private String menuType;
    @Column(name = "fav_title")
    private String favTitle;
    @Column(name = "fav_param")
    private String favParam;
    @Column(name = "sub_module_id")
    private String subModuleId;
    @Column(name = "min_trx")
    private String minTrx;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionValue() {
        return versionValue;
    }

    public void setVersionValue(String versionValue) {
        this.versionValue = versionValue;
    }

    public String getMenuType() {
        return menuType;
    }

    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    public String getFavTitle() {
        return favTitle;
    }

    public void setFavTitle(String favTitle) {
        this.favTitle = favTitle;
    }

    public String getFavParam() {
        return favParam;
    }

    public void setFavParam(String favParam) {
        this.favParam = favParam;
    }

    public String getSubModuleId() {
        return subModuleId;
    }

    public void setSubModuleId(String subModuleId) {
        this.subModuleId = subModuleId;
    }

    public String getMinTrx() {
        return minTrx;
    }

    public void setMinTrx(String minTrx) {
        this.minTrx = minTrx;
    }
}
