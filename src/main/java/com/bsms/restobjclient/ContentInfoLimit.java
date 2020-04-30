package com.bsms.restobjclient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentInfoLimit {
	private String key;
	private String trxAmtLimit_display;
	private String dailyAmtLimit_display;
	private String remainingLimit_display;
	
	public ContentInfoLimit(String key,String trxAmtLimit_display,String dailyAmtLimit_display,String remainingLimit_display) {
				
				this.key=key;
				this.trxAmtLimit_display=trxAmtLimit_display;
				this.dailyAmtLimit_display=dailyAmtLimit_display;
				this.remainingLimit_display=remainingLimit_display;
			
	}
}
