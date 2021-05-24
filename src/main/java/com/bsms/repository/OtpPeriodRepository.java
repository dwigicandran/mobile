package com.bsms.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bsms.domain.OtpPeriod;

@Repository
public interface OtpPeriodRepository extends CrudRepository<OtpPeriod, String> {

	List<OtpPeriod> findAllByLang(String lang);
	
}
