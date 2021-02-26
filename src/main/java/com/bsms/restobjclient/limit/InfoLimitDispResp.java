package com.bsms.restobjclient.limit;

import java.io.Serializable;
import java.util.List;

import com.bsms.restobj.MbApiContentResp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class InfoLimitDispResp implements MbApiContentResp, Serializable {

	private String category;
	private List<ContentInfoLimit> content = null;

	public InfoLimitDispResp(String category,List<ContentInfoLimit> content) {
	
		this.category = category;
		this.content = content;
	}

}
