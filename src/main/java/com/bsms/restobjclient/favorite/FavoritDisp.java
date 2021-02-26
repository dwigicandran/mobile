package com.bsms.restobjclient.favorite;

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
public class FavoritDisp implements MbApiContentResp, Serializable {
	
	private static final long serialVersionUID = 5074717308798811211L;
	
	private List<Favorit> content=null;
	
	public FavoritDisp(List<Favorit> favorit) {
				
				this.content=favorit;
				
	}
	
}
