package com.bsms.restobjclient.infoERGold;

import java.io.Serializable;

import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Data
public class ListContentER implements Serializable, MbApiContentResp {
	private String id;
	private String buyrate;
	private String sellrate;
	private String midrate;
	
}
