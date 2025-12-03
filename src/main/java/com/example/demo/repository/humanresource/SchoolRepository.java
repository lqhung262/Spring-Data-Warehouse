package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findBySourceId(String sourceId);

    Optional<School> findBySchoolCode(String code);

    Optional<School> findByName(String name);

    // Batch queries for bulk upsert
    List<School> findBySourceIdIn(Collection<String> sourceIds);

    List<School> findByNameIn(Collection<String> names);

    List<School> findBySchoolCodeIn(Collection<String> codes);
}
