package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {
    Optional<JobPosition> findBySourceId(String sourceId);

    Optional<JobPosition> findByJobPositionCode(String code);

    Optional<JobPosition> findByName(String name);

    // Batch queries for bulk upsert
    List<JobPosition> findBySourceIdIn(Collection<String> sourceIds);

    List<JobPosition> findByNameIn(Collection<String> names);

    List<JobPosition> findByJobPositionCodeIn(Collection<String> codes);
}
