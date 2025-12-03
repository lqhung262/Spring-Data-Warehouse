package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.OldWard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface OldWardRepository extends JpaRepository<OldWard, Long> {
    Optional<OldWard> findBySourceId(String sourceId);

    Optional<OldWard> findByName(String name);

    // Batch queries for bulk upsert
    List<OldWard> findBySourceIdIn(Collection<String> sourceIds);

    List<OldWard> findByNameIn(Collection<String> names);

    List<OldWard> findByOldDistrict_OldDistrictId(Long oldDistrictId);

    long countByOldDistrict_OldDistrictId(Long oldDistrictId);

    List<OldWard> findByWard_WardId(Long wardId);

    long countByWard_WardId(Long wardId);
}
