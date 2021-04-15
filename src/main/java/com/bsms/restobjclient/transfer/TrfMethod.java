package com.bsms.restobjclient.transfer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrfMethod {
	private String key;
	private String value;
	
	public TrfMethod(String key,String value) {
				
				this.key=key;
				this.value=value;
			
	}

}
