package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.BloodGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BloodGroupRepository extends JpaRepository<BloodGroup, Long> {
    Optional<BloodGroup> findBySourceId(String sourceId);

    Optional<BloodGroup> findByBloodGroupCode(String code);

    Optional<BloodGroup> findByName(String name);

    // Batch queries for bulk upsert
    List<BloodGroup> findBySourceIdIn(Collection<String> sourceIds);

    List<BloodGroup> findByNameIn(Collection<String> names);

    List<BloodGroup> findByBloodGroupCodeIn(Collection<String> codes);
}
