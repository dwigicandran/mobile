package com.bsms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bsms.domain.CardMapping;

public interface CardmappingRepository extends CrudRepository<CardMapping, Long> {

	CardMapping findTopByCustomeridAndAccountnumber(Long customerId, String accountNumber);
	
	CardMapping findOneByCustomeridAndAccountnumber(Long customerId, String accountNumber);
	
	CardMapping findTopByCustomerid(Long customerId);
	
	List<CardMapping> findAccountnumberByCustomerid(Long customerId);
	
	@Query(value = "select top 1 pinoffset from cardmapping with (nolock) where customerid= :ID", nativeQuery = true)
	String getPinoffsetByID(@Param("ID") String id);
	
	@Query(value = "SELECT count(1) from cardmapping with (nolock) where customerid=:customer_id and pinoffset is not null", nativeQuery = true)
	Integer getCountByCustomerId(@Param("customer_id") String customerId);
	
	@Query(value = "SELECT distinct cardnumber from cardmapping with (nolock) where customerid=:customer_id", nativeQuery = true)
	String getCardnumberByCustomerId(@Param("customer_id") String customer_id);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "UPDATE CardMapping set PinOffset=:pinoffset where cardnumber=:card_number")
	void updPinoffsetByCardnum(@Param("pinoffset") String pinoffset, @Param("card_number") String cardNumber);
}
