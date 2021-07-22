package com.bsms.domain;


//Addition By Dwi S - February 2020

import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "Favorite")
@ToString
public class Favorite {

    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "id_fav")
    private String id_fav;
    @Column(name = "created")
    private Date created;
    @Column(name = "fav_title")
    private String fav_title;
    @Column(name = "msisdn")
    private String msisdn;
    @Column(name = "submodul_id")
    private String submodul_id;
    @Column(name = "destinationAccountNumber")
    private String destinantionAccountNumber;
    @Column(name = "destinationAccountName")
    private String destinationAccountName;
    @Column(name = "destinationBank")
    private String destinationBank;
    @Column(name = "bankName")
    private String bankName;
    @Column(name = "trfMethod")
    private String trfMethod;
    @Column(name = "billKey1")
    private String billkey1;
    @Column(name = "billCode")
    private String billCode;
    @Column(name = "billerId")
    private String billerid;
    @Column(name = "cardno")
    private String cardno;
    @Column(name = "typebank")
    private String typeBank;
    @Column(name = "billName")
    private String billName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getId_fav() {
        return id_fav;
    }

    public void setId_fav(String id_fav) {
        this.id_fav = id_fav;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getFav_title() {
        return fav_title;
    }

    public void setFav_title(String fav_title) {
        this.fav_title = fav_title;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getSubmodul_id() {
        return submodul_id;
    }

    public void setSubmodul_id(String submodul_id) {
        this.submodul_id = submodul_id;
    }

    public String getDestinantionAccountNumber() {
        return destinantionAccountNumber;
    }

    public void setDestinantionAccountNumber(String destinantionAccountNumber) {
        this.destinantionAccountNumber = destinantionAccountNumber;
    }

    public String getDestinationAccountName() {
        return destinationAccountName;
    }

    public void setDestinationAccountName(String destinationAccountName) {
        this.destinationAccountName = destinationAccountName;
    }

    public String getDestinationBank() {
        return destinationBank;
    }

    public void setDestinationBank(String destinationBank) {
        this.destinationBank = destinationBank;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getTrfMethod() {
        return trfMethod;
    }

    public void setTrfMethod(String trfMethod) {
        this.trfMethod = trfMethod;
    }

    public String getBillkey1() {
        return billkey1;
    }

    public void setBillkey1(String billkey1) {
        this.billkey1 = billkey1;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public String getBillerid() {
        return billerid;
    }

    public void setBillerid(String billerid) {
        this.billerid = billerid;
    }

    public String getCardno() {
        return cardno;
    }

    public void setCardno(String cardno) {
        this.cardno = cardno;
    }

    public String getTypeBank() {
        return typeBank;
    }

    public void setTypeBank(String typeBank) {
        this.typeBank = typeBank;
    }

    public String getBillName() {
        return billName;
    }

    public void setBillName(String billName) {
        this.billName = billName;
    }
}
