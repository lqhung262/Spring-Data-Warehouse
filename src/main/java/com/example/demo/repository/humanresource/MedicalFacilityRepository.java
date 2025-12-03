package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.MedicalFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalFacilityRepository extends JpaRepository<MedicalFacility, Long> {
    Optional<MedicalFacility> findBySourceId(String sourceId);

    Optional<MedicalFacility> findByMedicalFacilityCode(String code);

    Optional<MedicalFacility> findByName(String name);

    // Batch queries for bulk upsert
    List<MedicalFacility> findBySourceIdIn(Collection<String> sourceIds);

    List<MedicalFacility> findByNameIn(Collection<String> names);

    List<MedicalFacility> findByMedicalFacilityCodeIn(Collection<String> codes);

    // Count methods for cascade delete checks
    long countByProvinceCity_ProvinceCityId(Long provinceCityId);

    @Query("SELECT mf.provinceCity.provinceCityId, COUNT(mf) FROM MedicalFacility mf " +
            "WHERE mf.provinceCity.provinceCityId IN :provinceCityIds " +
            "GROUP BY mf.provinceCity.provinceCityId")
    List<Object[]> countByProvinceCityIdIn(@Param("provinceCityIds") List<Long> provinceCityIds);
}
