package com.bsms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bsms.domain.Customer;

@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface CustomerRepository extends CrudRepository<Customer, Long> {
	
	Customer findByMsisdn(String msisdn);
	
	long countByMsisdn(String msisdn);
	
	@Query(value = "select msisdn from Customer where id = :ID")
	String getDataByID(@Param("ID") Long id);
	
	@Query(value = "select failedpincount from Customer where ID = :id")
	Integer getFailedPINCountById(@Param("id") Long id);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "update Customer set failedpincount = :valpin where msisdn = :msisdn")
	void updatePINCountById(@Param("valpin") Integer valpin, @Param("msisdn") String msisdn);
	
	@Query(value = "SELECT ID, Name, ActivationCode, email, MSISDN, TAK, MACHEX, CreateOTPDate "
		    		+ "FROM Customer with (NOLOCK) where MSISDN in (:msisdn1,:msisdn2)", nativeQuery = true)
	Customer getByMsisdn(@Param("msisdn1") String msisdn1, @Param("msisdn2") String msisdn2);

	
}
