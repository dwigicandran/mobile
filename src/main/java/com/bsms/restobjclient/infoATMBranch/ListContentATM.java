package com.bsms.restobjclient.infoATMBranch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class ListContentATM {

	private String atm_name;
	private String atm_address;
	private String longitude;
	private String latitude;
	private String google_map;
	
}
