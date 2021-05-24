package com.bsms.repository;

import com.bsms.domain.MbLimit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MbLimitRepository extends CrudRepository<MbLimit, String> {
    Optional<MbLimit> findByCustomerTypeAndTrxTypeAndEnabled(Integer customerType, Integer trxType, String enabled);
}
