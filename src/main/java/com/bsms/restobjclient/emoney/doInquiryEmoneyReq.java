package com.bsms.restobjclient.emoney;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter

public class doInquiryEmoneyReq implements Serializable {

    private static final long serialVersionUID = -2795121370329565439L;

    private String correlationId;
    private String transactionId;
    private String deliveryChannel;
    private String sourceAccountNumber;
    private String sourceAccountName;
    private String cardNo;
    private String encryptedPinBlock;
    private String description;
    private String amount;
    private String stan;
    private String pan;
    private String language;
    private String cardAcceptorTerminal;
    private String cardAcceptorMerchantId;
    private String currency;
    private String beneficiaryInstitutionCode;
}
