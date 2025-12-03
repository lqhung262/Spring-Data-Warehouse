package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.MaritalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaritalStatusRepository extends JpaRepository<MaritalStatus, Long> {
    Optional<MaritalStatus> findBySourceId(String sourceId);

    Optional<MaritalStatus> findByMaritalStatusCode(String code);

    Optional<MaritalStatus> findByName(String name);

    // Batch queries for bulk upsert
    List<MaritalStatus> findBySourceIdIn(Collection<String> sourceIds);

    List<MaritalStatus> findByNameIn(Collection<String> names);

    List<MaritalStatus> findByMaritalStatusCodeIn(Collection<String> codes);
}
