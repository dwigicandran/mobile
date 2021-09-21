package com.bsms.repository;

import com.bsms.domain.MbActivation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MbActivationRepository extends CrudRepository<MbActivation, String> {

	@Query(
		    value = "SELECT SUM(count)FROM MB_Activation WHERE msisdn = :msisdn",
		    nativeQuery = true)
	String findByMsisdn(@Param("msisdn") String msisdn);

	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM MB_Activation where msisdn= :msisdn", nativeQuery = true)
	void deleteByMsisdn(@Param("msisdn") String msisdn);

	@Modifying(clearAutomatically = true)
	@Query(value = "insert into MB_Activation (msisdn, count) values (:msisdn, :count)", nativeQuery = true)
	void saveActivation(@Param("msisdn") String msisdn, @Param("count") String count);
	
}
