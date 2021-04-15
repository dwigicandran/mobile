package com.bsms.repository;

import com.bsms.domain.MbInstitutionAcademic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MbInstitutionAcademicRepository extends JpaRepository<MbInstitutionAcademic, Integer> {

    MbInstitutionAcademic findByPrefix(String prefix);

}
