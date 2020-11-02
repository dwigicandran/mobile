package com.bsms.restobjclient.infoERGold;

import java.io.Serializable;
import java.util.List;

import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class ListContentGold implements Serializable, MbApiContentResp {

	private List<ListContentReguler> reguler;
	private List<ListContentBatik> batik;
	private List<ListContentDinar> dinar;
	
}
