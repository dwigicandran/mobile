package com.bsms.restobjclient.transfer;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class InternalTrfReq implements Serializable  {

	private static final long serialVersionUID = -1030898185421052239L;
	
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
	private String pan;
	private String cardAcceptorTerminal;
	private String cardAcceptorMerchantId;
	private String currency;
	private String language;

	
}
