package com.bsms.restobjclient.emoney;

import java.io.Serializable;
import java.util.List;

import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class ContentEmoneydoUpdate implements Serializable, MbApiContentResp {
	private String response;
	private String apiVersion;
	private String jwt;
	private ListContentdoUpdate responseBalance;
	private ListContentReversal responseReversal;
}
