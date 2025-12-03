package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobTitleRepository extends JpaRepository<JobTitle, Long> {
    Optional<JobTitle> findBySourceId(String sourceId);

    Optional<JobTitle> findByJobTitleCode(String code);

    Optional<JobTitle> findByName(String name);

    // Batch queries for bulk upsert
    List<JobTitle> findBySourceIdIn(Collection<String> sourceIds);

    List<JobTitle> findByNameIn(Collection<String> names);

    List<JobTitle> findByJobTitleCodeIn(Collection<String> codes);

}
