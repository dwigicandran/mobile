package com.bsms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MB_LIMIT_VIEW")
public class MbLimit {

	@Id
    @Column(name = "id")
    private String id;
    @Column(name = "customer_type")
    private Integer customerType;
    @Column(name = "trx_type")
    private Integer trxType;
    @Column(name = "trx_amount_limit")
    private String trxAmountLimit;
    @Column(name = "daily_amount_limit")
    private String dailyAmountLimit;
    @Column(name = "enabled")
    private String enabled;
    @Column(name = "last_access_date")
    private String lastAccessDate;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "description")
    private String description;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCustomerType() {
        return customerType;
    }

    public void setCustomerType(Integer customerType) {
        this.customerType = customerType;
    }

    public Integer getTrxType() {
        return trxType;
    }

    public void setTrxType(Integer trxType) {
        this.trxType = trxType;
    }

    public String getTrxAmountLimit() {
        return trxAmountLimit;
    }

    public void setTrxAmountLimit(String trxAmountLimit) {
        this.trxAmountLimit = trxAmountLimit;
    }

    public String getDailyAmountLimit() {
        return dailyAmountLimit;
    }

    public void setDailyAmountLimit(String dailyAmountLimit) {
        this.dailyAmountLimit = dailyAmountLimit;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(String lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
	
}
