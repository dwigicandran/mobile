package com.bsms.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "MB_smsactivation")
@Data
public class MbSmsActivation {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String id;
	
	private String msisdn;
	
	private String message;
	
	private String dateReceived;
	
	private String isverified;
	
	private String dateVerified;
	
}
