package com.bsms.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MB_appcontent")
public class MbAppContent {

	@Id
	private String langId;
	
	private long lang_type;
	private String description;
	private String language;
	private String keterangan;
	
	public long getLang_type() {
		return lang_type;
	}
	public void setLang_type(long lang_type) {
		this.lang_type = lang_type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getKeterangan() {
		return keterangan;
	}
	public void setKeterangan(String keterangan) {
		this.keterangan = keterangan;
	}
	public String getLangId() {
		return langId;
	}
	public void setLangId(String langId) {
		this.langId = langId;
	}
	
	
}
