package com.bsms.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bsms.domain.Customer;

@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface CustomerRepository extends CrudRepository<Customer, Long> {
	
	Customer findByMsisdn(String msisdn);
	long countByMsisdn(String msisdn);
	
}
