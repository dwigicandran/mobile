package com.bsms.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bsms.domain.Security;

public interface SecurityRepository extends CrudRepository<Security, Long> {

	Security findByCustomerId(Long customerId);
	
	Security findByMbSessionId(String mbSessionId);
	
//	Security deleteByCustomerId(long customerId);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM Security where customerId = :customerId")
	void deleteByCustId(@Param("customerId") long customerId);
	
}
