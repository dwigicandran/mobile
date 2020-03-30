package com.bsms.repository;

import org.springframework.data.repository.CrudRepository;

import com.bsms.domain.MbAppContent;

public interface MbAppContentRepository extends CrudRepository<MbAppContent, String> {

	MbAppContent findByLangIdAndLanguage(String langId, String language);
	
}
