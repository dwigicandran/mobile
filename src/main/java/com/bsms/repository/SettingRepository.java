package com.bsms.repository;

import com.bsms.domain.Setting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface SettingRepository extends CrudRepository<Setting, String> {


    @Query(value = "SELECT value from setting with (nolock) where name=:name", nativeQuery = true)
    String getValueByName(@Param("name") String name);


}
