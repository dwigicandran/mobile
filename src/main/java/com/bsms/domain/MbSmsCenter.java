package com.bsms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

//By Muhammad Hadiansyah - Maret 2021

@Entity
@Table(name="MB_smscenter")
@Data
public class MbSmsCenter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;

	@Column(name="msisdn_prefix")
	private String msisdnPrefix;
	
	@Column(name="provider_name")
	private String providerName;
	
	@Column(name="sms_center")
	private String smsCenter;
	
}
