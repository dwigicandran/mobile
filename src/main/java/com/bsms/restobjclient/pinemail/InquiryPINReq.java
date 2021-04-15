package com.bsms.restobjclient.pinemail;

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
public class InquiryPINReq implements Serializable {

	private static final long serialVersionUID = -7427054291035764356L;
	
	private String customer_id;
	private String language;
	private String zpk;
	private String pin;
	private String pinoffset;
	private String newpin;
	private String pan;
	

}
