package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.JobRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRankRepository extends JpaRepository<JobRank, Long> {
    Optional<JobRank> findBySourceId(String sourceId);

    Optional<JobRank> findByJobRankCode(String code);

    Optional<JobRank> findByName(String name);

    // Batch queries for bulk upsert
    List<JobRank> findBySourceIdIn(Collection<String> sourceIds);

    List<JobRank> findByNameIn(Collection<String> names);

    List<JobRank> findByJobRankCodeIn(Collection<String> codes);
}
