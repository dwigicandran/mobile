package com.bsms.repository;

import com.bsms.domain.SpMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

//Addition By Dwi S - September 2020

@Repository
public interface SpMerchantRepository extends JpaRepository<SpMerchant, String> {

    SpMerchant findByMerchantCode(String merchantCode);

    SpMerchant findBySpMerchantId(String merchantId);

    @Query(value = "SELECT * FROM SuperApp.dbo.SP_Merchant WHERE  sp_merchant_id = :merchantId", nativeQuery = true)
    List<SpMerchant> findAllSpMerchantByMerchantId(@Param("merchantId") String merchantId);

}
