package com.bsms.restobjclient.ziswaf;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class ListContentGetListProjectWakaf {

	private String key;
	private String value;
	private String desc;
	private String imageurl;
	
}
