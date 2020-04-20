package com.bsms.domain;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

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
	
	@Column(name="TAK")
	private String tak;
	
	@Column(name="MACHEX")
	private String machex;
	
}
