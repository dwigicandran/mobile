package com.bsms.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.bsms.domain.OtpNumTrx;

@Repository
public interface OtpNumTrxRepository extends CrudRepository<OtpNumTrx, Integer> {

	List<OtpNumTrx> findAllByLang(String lang);
	
}
