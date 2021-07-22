package com.bsms.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bsms.domain.MbSmsCenter;

@Repository
public interface MbSmsCenterRepository extends CrudRepository<MbSmsCenter, String> {
	
	MbSmsCenter findByMsisdnPrefix(String msisdnPrefix);
	
	List<MbSmsCenter> findAll();

}
