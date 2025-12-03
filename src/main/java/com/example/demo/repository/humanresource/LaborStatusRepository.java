package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.LaborStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LaborStatusRepository extends JpaRepository<LaborStatus, Long> {
    Optional<LaborStatus> findBySourceId(String sourceId);

    Optional<LaborStatus> findByLaborStatusCode(String code);

    Optional<LaborStatus> findByName(String name);

    // Batch queries for bulk upsert
    List<LaborStatus> findBySourceIdIn(Collection<String> sourceIds);

    List<LaborStatus> findByNameIn(Collection<String> names);

    List<LaborStatus> findByLaborStatusCodeIn(Collection<String> codes);
}
