package com.bsms.domain;

import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//Addition By Dwi S - September 2020

@Entity
@Table(name = "SP_Merchant")
@ToString
public class SpMerchant {
    private String merchantCode;
    private Integer serviceprovider;
    private String spMerchantId;
    private String spExtData;

    @Id
    @Column(name = "merchant_code")
    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public Integer getServiceprovider() {
        return serviceprovider;
    }

    public void setServiceprovider(Integer serviceprovider) {
        this.serviceprovider = serviceprovider;
    }

    @Column(name = "sp_merchant_id")
    public String getSpMerchantId() {
        return spMerchantId;
    }

    public void setSpMerchantId(String spMerchantId) {
        this.spMerchantId = spMerchantId;
    }

    @Column(name = "sp_ext_data")
    public String getSpExtData() {
        return spExtData;
    }

    public void setSpExtData(String spExtData) {
        this.spExtData = spExtData;
    }
}
