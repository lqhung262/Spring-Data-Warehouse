package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EducationLevel;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EducationLevelRepository extends JpaRepository<EducationLevel, Long> {
    Optional<EducationLevel> findBySourceId(String sourceId);

    Optional<EducationLevel> findByEducationLevelCode(String code);

    Optional<EducationLevel> findByName(String name);

    // Batch queries for bulk upsert
    List<EducationLevel> findBySourceIdIn(Collection<String> sourceIds);

    List<EducationLevel> findByNameIn(Collection<String> names);

    List<EducationLevel> findByEducationLevelCodeIn(Collection<String> codes);
}
