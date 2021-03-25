package com.bsms.repository;

import com.bsms.domain.MbMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Addition By Dwi S - Februari 2021

@Repository
public interface MbMerchantRepository extends JpaRepository<MbMerchant, String> {

    MbMerchant findByCode(String code);

}
