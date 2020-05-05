package com.bsms.restobjclient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentInfoLimit {
	private String key;
	private String trxAmtLimit;
	private String dailyAmtLimit;
	private String limitUsed;
	private String remainingLimit;
	
	public ContentInfoLimit(String key,String trxAmtLimit_display,String dailyAmtLimit_display,String limitUsed,String remainingLimit_display) {
				
				this.key=key;
				this.trxAmtLimit=trxAmtLimit_display;
				this.dailyAmtLimit=dailyAmtLimit_display;
				this.limitUsed=limitUsed;
				this.remainingLimit=remainingLimit_display;
			
	}
}
