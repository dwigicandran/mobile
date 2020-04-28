package com.bsms.restobjclient;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class CreatePinReq implements Serializable {
	
	private static final long serialVersionUID = -1957143381189203316L;
	
	private String zpk;
	private String pin;
	@JsonProperty("card_number")
	private String cardNumber;

}
