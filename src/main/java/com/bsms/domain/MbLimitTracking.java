package com.bsms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "MB_LIMITTRACKING_VIEW")
public class MbLimitTracking {

	@Id
    @JsonProperty("id")
    private Long id;
    @Column(name = "msisdn")
    private String msisdn;
    @Column(name = "trx_type")
    private Integer trxType;
    @Column(name = "last_trx_date")
    private Date lastTrxDate;
    @Column(name = "total_amount")
    private String totalAmount;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMsisdn() {
        return msisdn;
    }
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    public String getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getLastTrxDate() {
        return lastTrxDate;
    }

    public void setLastTrxDate(Date lastTrxDate) {
        this.lastTrxDate = lastTrxDate;
    }

    public Integer getTrxType() {
        return trxType;
    }

    public void setTrxType(Integer trxType) {
        this.trxType = trxType;
    }
	
}
