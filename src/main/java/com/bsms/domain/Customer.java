package com.bsms.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "Customer")
public class Customer {

	@Id
	@Column(name="ID")
	private Long id;
	
	@Column(name="Name")
	private String name;
	
	@Column(name="MSISDN")
	private String msisdn;
	
	@Column(name="imei")
	private String imei;
	
	@Column(name="Activationcode")
	private String activationcode;
	
	@Column(name="email")
	private String email;
	
	@Column(name="createotpdate")
	private String createotpdate;

	@Column(name="createdate")
	private String createdate;
	
	@Column(name="TAK")
	private String tak;
	
	@Column(name="MACHEX")
	private String machex;
	
	private String failedpincount;
	
}
