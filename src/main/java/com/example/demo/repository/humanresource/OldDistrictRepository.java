package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OldDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OldDistrictRepository extends JpaRepository<OldDistrict, Long> {
    Optional<OldDistrict> findBySourceId(String sourceId);

    Optional<OldDistrict> findByName(String name);

    // Batch queries for bulk upsert
    List<OldDistrict> findBySourceIdIn(Collection<String> sourceIds);

    List<OldDistrict> findByNameIn(Collection<String> names);

    // For cascade delete checks
    long countByWard_WardId(Long wardId);

    long countByOldProvinceCity_OldProvinceCityId(Long oldProvinceCityId);


}
