package com.bsms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.bsms.domain.CardMapping;

public interface CardmappingRepository extends CrudRepository<CardMapping, Long> {

	CardMapping findByCustomeridAndAccountnumber(Long customerId, String accountNumber);
	
	CardMapping findOneByCustomeridAndAccountnumber(Long customerId, String accountNumber);
	
	List<CardMapping> findAccountnumberByCustomerid(Long customerId);
	
	@Query(value = "select pinoffset from cardmapping with (nolock) where customerid= :ID", nativeQuery = true)
	String getPinoffsetByID(@Param("ID") String id);
	
	
}
