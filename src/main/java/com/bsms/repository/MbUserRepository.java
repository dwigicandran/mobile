package com.bsms.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bsms.domain.MbApiUser;

public interface MbUserRepository extends MongoRepository<MbApiUser, String> {
	
	public MbApiUser findOneByUsernameAndPassword(String username, String password);
	
	public MbApiUser findOneByUsername(String username);

}
