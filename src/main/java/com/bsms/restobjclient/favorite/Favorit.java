package com.bsms.restobjclient.favorite;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Favorit {
	private String key;
	private String value;
	
	public Favorit(String key,String value) {
				
				this.key=key;
				this.value=value;
			
	}
}
