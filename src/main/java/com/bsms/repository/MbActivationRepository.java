package com.bsms.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bsms.domain.MbActivation;

public interface MbActivationRepository extends CrudRepository<MbActivation, String> {

	@Query(
		    value = "SELECT SUM(count)FROM MB_Activation WHERE msisdn = :msisdn",
		    nativeQuery = true)
	String findByMsisdn(@Param("msisdn") String msisdn);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM MbActivation where msisdn= :msisdn")
	void deleteByMsisdn(@Param("msisdn") String msisdn);
	
}
