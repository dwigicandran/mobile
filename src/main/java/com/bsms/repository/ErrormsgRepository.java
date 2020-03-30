package com.bsms.repository;

import org.springframework.data.repository.CrudRepository;

import com.bsms.domain.ErrorMessage;

public interface ErrormsgRepository extends CrudRepository<ErrorMessage, String> {

	ErrorMessage findByCodeAndLanguage(String responseCode, String Language);
	
}
