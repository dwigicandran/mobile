package com.bsms.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bsms.domain.MbApiTxLog;

public interface MbTxLogRepository extends  MongoRepository<MbApiTxLog, String> {

}
