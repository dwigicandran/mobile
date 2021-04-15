package com.bsms.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.bsms.domain.MbApiTxLog;

import java.util.Optional;

public interface MbTxLogRepository extends MongoRepository<MbApiTxLog, String> {
    Optional<MbApiTxLog> findById(String id);
}
