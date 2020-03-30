package com.bsms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.bsms.domain.CardMapping;

public interface CardmappingRepository extends CrudRepository<CardMapping, Long> {

	CardMapping findByCustomeridAndAccountnumber(Long customerId, String accountNumber);
	
	CardMapping findOneByCustomeridAndAccountnumber(Long customerId, String accountNumber);
	
	List<CardMapping> findAccountnumberByCustomerid(Long customerId);
	
	
}
