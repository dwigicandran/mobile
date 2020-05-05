package com.bsms.restobjclient;

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

	private List<ContentInfoLimit> content = null;

	public InfoLimitDispResp(List<ContentInfoLimit> content) {
	
		this.content = content;
	}

}
