package com.bsms.restobjclient.transfer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentIntTrf {
	private String key;
	private String value;
	private String desc;
	
	public ContentIntTrf(String key,String value,String desc) {
				
				this.key=key;
				this.value=value;
				this.desc=desc;
			
		// TODO Auto-generated constructor stub
	}
}
