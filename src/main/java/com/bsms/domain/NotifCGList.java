package com.bsms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Notif_Cglist")
public class NotifCGList {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long idCgl;
	
	@Column(name = "MSISDN")
	private String msisdn;
	@Column(name = "ID_CG")
	private Long idCg;
	
	public Long getIdCgl() {
		return idCgl;
	}
	public void setIdCgl(Long idCgl) {
		this.idCgl = idCgl;
	}
	public String getMsisdn() {
		return msisdn;
	}
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}
	public Long getIdCg() {
		return idCg;
	}
	public void setIdCg(Long idCg) {
		this.idCg = idCg;
	}
	
}
