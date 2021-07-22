package com.bsms.domain;

import java.io.Serializable;
import java.security.Principal;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mb_user")
public class MbApiUser implements Principal, Serializable {

	@Id
    private String id;

    @NotBlank(message = "Username must be not blank.")
    private String username;

    @NotBlank(message = "Password must be not blank.")
    private String password;

    @NotBlank(message = "Batch token must be not blank.")
    @Size(min=8, max=17)
    private String batchToken;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBatchToken() {
		return batchToken;
	}

	public void setBatchToken(String batchToken) {
		this.batchToken = batchToken;
	}
	
}
