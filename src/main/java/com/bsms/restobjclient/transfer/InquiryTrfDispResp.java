package com.bsms.restobjclient.transfer;

import java.io.Serializable;
import java.util.List;

import com.bsms.restobj.MbApiContentResp;
import com.dto.inquirytransfer.Content;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class InquiryTrfDispResp implements MbApiContentResp, Serializable {

    private static final long serialVersionUID = 5074717308798811211L;
    private String accountId;
    private String accountName;
    private String paymentId;
    private String paymentName;
    private String transactionId;

    //add properties for skn transfer
    private String info;

    private List<ContentInqTrf> content = null;

    public InquiryTrfDispResp(String accountId, String accountName, String paymentId, String paymentName,
                              List<ContentInqTrf> content, String trx_id, String info) {

        this.accountId = accountId;
        this.accountName = accountName;
        this.paymentId = paymentId;
        this.paymentName = paymentName;
        this.content = content;
        this.transactionId = trx_id;
        this.info = info;

    }

}
