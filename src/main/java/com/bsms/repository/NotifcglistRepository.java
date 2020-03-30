package com.bsms.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.bsms.domain.NotifCGList;

@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface NotifcglistRepository extends CrudRepository<NotifCGList, Long> {

	NotifCGList findByMsisdnAndIdCg(String noHp, Long flag);
	
	long countByMsisdn(String noHp);
	
}
