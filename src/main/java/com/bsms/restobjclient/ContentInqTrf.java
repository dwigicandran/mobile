package com.bsms.restobjclient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentInqTrf {
	private String key;
	private String value;
	
	public ContentInqTrf(String key,String value) {
				
				this.key=key;
				this.value=value;
			
		// TODO Auto-generated constructor stub
	}
}
