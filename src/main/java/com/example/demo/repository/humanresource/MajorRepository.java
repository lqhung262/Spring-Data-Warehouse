package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major, Long> {
    Optional<Major> findBySourceId(String sourceId);

    Optional<Major> findByMajorCode(String code);

    Optional<Major> findByName(String name);

    // Batch queries for bulk upsert
    List<Major> findBySourceIdIn(Collection<String> sourceIds);

    List<Major> findByNameIn(Collection<String> names);

    List<Major> findByMajorCodeIn(Collection<String> codes);
}
