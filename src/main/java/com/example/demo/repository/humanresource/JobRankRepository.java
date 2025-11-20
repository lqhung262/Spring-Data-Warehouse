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

//    /**
//     * Tối ưu cho Upsert: Tìm tất cả jobRanks tồn tại trong 1 câu query.
//     */
//    List<JobRank> findByJobRankCodeIn(Collection<String> jobRankCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 jobRank bằng jobRankCode
//     */
//    Optional<JobRank> findByJobRankCode(String jobRankCode);
//
//    Long countByJobRankIdIn(Collection<Long> jobRankIds);
}
