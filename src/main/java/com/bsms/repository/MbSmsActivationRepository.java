package com.bsms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bsms.domain.MbSmsActivation;

public interface MbSmsActivationRepository extends CrudRepository<MbSmsActivation, String>{

	MbSmsActivation findByMsisdnAndIsverified(String msisdn, String isverified);
	
	MbSmsActivation findByMsisdnAndIsverifiedAndDateReceived(String msisdn, String isverified, String dateReceived);
	
	@Query(value = "SELECT top 1 * from mb_smsactivation with (NOLOCK) "
    		+ " where msisdn in (:msisdn1,:msisdn2) and isverified=:isVerified and DATEDIFF(s, date_received, GETDATE())<=:time order by date_received desc", nativeQuery = true)
	List<MbSmsActivation> getDataByMsisdn(@Param("msisdn1") String msisdn1, @Param("msisdn2") String msisdn2, @Param("isVerified") String isVerified, @Param("time") long time);
	

	
}
