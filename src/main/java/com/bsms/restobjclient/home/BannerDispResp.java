package com.bsms.restobjclient.home;

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
public class BannerDispResp implements MbApiContentResp, Serializable {
	
	private static final long serialVersionUID = 5074717308798811211L;
	
	private List<Banner1> banner1=null;
	private List<Banner2> banner2=null;
	
	public BannerDispResp(List<Banner1> banner1,List<Banner2> banner2) {
				
				this.banner1=banner1;
				this.banner2=banner2;
				
	}
	
}
