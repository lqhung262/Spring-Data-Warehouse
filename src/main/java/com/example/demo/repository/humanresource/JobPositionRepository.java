package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả jobPositions tồn tại trong 1 câu query.
     */
    List<JobPosition> findByJobPositionCodeIn(Collection<String> jobPositionCodes);

    /**
     * Dùng cho Upsert: Tìm 1 jobPosition bằng jobPositionCode
     */
    Optional<JobPosition> findByJobPositionCode(String jobPositionCode);

    Long countByJobPositionIdIn(Collection<Long> jobPositionIds);
}
