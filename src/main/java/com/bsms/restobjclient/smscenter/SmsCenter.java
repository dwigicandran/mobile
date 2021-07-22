package com.bsms.restobjclient.smscenter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsCenter {
	private String msisdnPrefix;
	private String providerName;
	private String smsCenterNumber;
	public SmsCenter(String prefix, String provider, String number) {
		this.msisdnPrefix = prefix;
		this.providerName = provider;
		this.smsCenterNumber = number;
	}
}
