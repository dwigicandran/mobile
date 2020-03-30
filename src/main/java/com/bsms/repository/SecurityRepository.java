package com.bsms.repository;

import org.springframework.data.repository.CrudRepository;

import com.bsms.domain.Security;

public interface SecurityRepository extends CrudRepository<Security, Long> {

	Security findByCustomerId(Long customerId);
	
	Security findByMbSessionId(String mbSessionId);
	
}
