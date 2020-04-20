package com.bsms.restobjclient;

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
public class OnlineTrfDispResp implements MbApiContentResp, Serializable {

	private static final long serialVersionUID = 5074717308798811211L;
	
	private String no;
	private String date;
	private String title;
	private String footer;
	
	private List<ContentIntTrf> content=null;
	
	public OnlineTrfDispResp(String no,String date,String title,String footer,
			List<ContentIntTrf> content) {
				
				this.no=no;
				this.date=date;
				this.title=title;
				this.footer=footer;
				this.content=content;
				
		// TODO Auto-generated constructor stub
	}
	
	
	
}
