package com.bsms.repository;

import com.bsms.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    @Query("SELECT f from Favorite f where f.submodul_id like ?1 and f.msisdn=?2")
    List<Favorite> findBySubmodul_idLikeAndMsisdn(String subModulId, String msisdn);

    @Query("SELECT f from Favorite f where f.submodul_id=?1 and f.msisdn=?2")
    List<Favorite> findAllBySubmodul_idAndMsisdn(String subModulId, String msisdn);

}
