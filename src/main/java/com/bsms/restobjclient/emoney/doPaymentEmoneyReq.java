package com.bsms.restobjclient.emoney;

import java.io.Serializable;

import com.bsms.repository.SecurityRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter

public class doPaymentEmoneyReq implements Serializable{

	private String customer_id;
    private String language;
    private String cardno;
    private String amount;
    private String account_number;
    private String account_name;
    private String transaction_id;
   
}
