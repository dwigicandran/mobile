package com.bsms.repository;

import com.bsms.domain.MbLimitTracking;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Repository
public interface MbLimitTrackingRepository extends CrudRepository<MbLimitTracking, String> {

    Optional<MbLimitTracking> findByMsisdnAndTrxType(String msisdn, Integer trxType);

    @Transactional
    @Modifying
    @Query("update MbLimitTracking SET last_trx_date=:last_trx_date, total_amount=:total_amount WHERE msisdn=:msisdn and trx_type=:trx_type")
    public void updateLimit(@Param("last_trx_date") Date last_trx_date, @Param("total_amount") String total_amount, @Param("msisdn") String msisdn, @Param("trx_type") Integer trx_type);
}
