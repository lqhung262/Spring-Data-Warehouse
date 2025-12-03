package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.WorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkLocationRepository extends JpaRepository<WorkLocation, Long> {
    Optional<WorkLocation> findBySourceId(String sourceId);

    Optional<WorkLocation> findByWorkLocationCode(String code);

    Optional<WorkLocation> findByName(String name);

    // Batch queries for bulk upsert
    List<WorkLocation> findBySourceIdIn(Collection<String> sourceIds);

    List<WorkLocation> findByNameIn(Collection<String> names);

    List<WorkLocation> findByWorkLocationCodeIn(Collection<String> codes);
}
