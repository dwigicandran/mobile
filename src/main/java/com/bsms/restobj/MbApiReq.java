package com.bsms.restobj;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MbApiReq implements Serializable {

    private static final long serialVersionUID = 6798772440711883353L;

    @JsonProperty("pin_offset")
    private String pinOffset;
    @JsonProperty("card_number")
    private String cardNumber;
    @JsonProperty("session_id")
    private String sessionId;
    private String session;

    private Integer customerLimitType;
    private String reqTime;
    private String reffNum;
    private String clientId;
    private String systemId;
    private String typeReq;
    private String channel;
    private String billingNo;
    private String billingAmount;
    private String denom;
    private String cifNum;
    private String branchId;
    private String terminalId;
    private String tellerId;
    private String traceNum;
    private String channelType;
    private String channelId;
    private String coreUsername;
    private String corePassword;
    private String coreCompany;
    private String colomName;
    private String idAccount;
    private String operand;
    private String deviceType;
    private String imei;
    private String language;
    private String menuId;

    private String customerId;
    private String osVersion;
    private String device;
    private String dateLocal;
    private String osType;
    private String username;
    private String password;
    private String numberOfTransaction;
    private String correlationId;
    private String transactionId;
    private String deliveryChannel;
    private String sourceAccountNumber;
    private String sourceAccountName;
    private String destinationAccountNumber;
    private String destinationAccountName;
    private String encryptedPinBlock;
    private String amount;
    private String description;
    private String stan;

    private String caId;
    private String zpkLMK;
    private String msisdn;
    private String customerName;
    private String customerEmail;
    private String notifType;
    private String token;
    private String activationCode;
    private String otp;

    private String modulId;
    private String versionName;
    private String ipAddress;
    private String requestType;
    private String versionValue;
    private String iccid;

    private String correlation_id;
    private String modul_id;
    private String sub_modul_id;
    private String public_key;
    private String ip_address;
    private String version_name;
    private String request_type;
    private String version_value;
    private String device_type;
    private String account_number;
    private String account_name;
    private String start_date;
    private String end_date;
    private String activation_code;
    private String request_data;
    private String customer_id;
    private String destinationBank;
    private String typeBank;
    private String trf_method;
    private String ref_no;
    private String fav_title;
    private String id_favorit;
    private String section_id;
    private String os_type;
    private String version_code;
    private String agent_id;

    //purchase and payment
    private String billkey1;
    private String billKey1;
    private String billkey2;
    private String billKey2;
    private String billCode;
    private String billerid;
    private String user_id;
    private String cardno;
    private String transaction_id;
    private String programId;

    //qris
    private String qrcode;
    private String mpan;
    private String merchantName;
    private String merchantLocation;
    private String percentage;
    private String admfee;
    private String tips;
    private String feetype;
    private String remark;

    //update emoney
    private String date_time;
    private String seq;
    private String cardAttr;
    private String lastBalance;
    private String cardInfo;
    private String cardUUID;
    private String code;
    private String menu_id;
    private String clientaddr;
    private String message;
    private String dataToSam;
    private String jwt;
    private String apiVersion;
    private String random;

    //infocenter
    private String latitude;
    private String longitude;

    //change email
    private String email;

    //change pin
    private String zpk;
    private String pinoffset;
    private String pin;
    private String newpin;
    private String pan;

    //opening account
    private String zakat;
    private String aopurpose;

    //qurban
    private String supplierId;
    private String denomId;
    private String onBehalf;
    private String phoneNumber;

    //umrah dan haji
    private String payment_type;
    private String payment_id;

    //card block
    private String card_type;

    //wakaf
    private String wakaf_type;
    private String nazhir_id;

    //portofolio
    private String info_type;
    private String cif;

    //setting
    private String prefix;

    //inbox notif
    private String nid;
    private String msgtype;
    private String mid;
    private String gmid;
    private String flag;

    //PDAM
    private String bcList;
    private String serviceId;

    //institution
    private String pay_code;
    private String category;

    //acop autosave
    private String flow_id;
    private String autosavetype;
    private String target_amount;
    private String deposit_amount;
    private String recurring_type;
    private String nod;
    private String account_title;
    private String account_type;
    private String availbal;
    private String totbal;
    private String autosaveacctno;
    private String acctname;
    private String card_no;
    private String card_info;

    private Object content;
    private MbApiContentReq reqContent;
    private MbApiContentReq reqData;

}
