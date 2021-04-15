package com.bsms.restobjclient.infoATMBranch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class ListContentBranch {

	private String branch_name;
	private String branch_address;
	private String longitude;
	private String latitude;
	private String google_map;
	
}
