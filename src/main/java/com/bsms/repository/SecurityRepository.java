package com.bsms.repository;

import com.bsms.domain.Security;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SecurityRepository extends CrudRepository<Security, Long> {

	Optional <Security> findByCustomerId(Long customerId);
	
	Security findByMbSessionId(String mbSessionId);
	
//	Security deleteByCustomerId(long customerId);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM Security where customerId = :customerId")
	void deleteByCustId(@Param("customerId") long customerId);

	@Query(value = "SELECT ChangeTime from Security where customerId = :customerId", nativeQuery = true)
	String getChangeTimeByCustomerId(@Param("customerId") long customerId);




	
}
