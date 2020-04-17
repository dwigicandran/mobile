package com.bsms.restobjclient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bank {
	private String key;
	private String value;
	
	public Bank(String key,String value) {
				
				this.key=key;
				this.value=value;
			
	}
}
