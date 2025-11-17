package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobTitleRepository extends JpaRepository<JobTitle, Long> {
//    /**
//     * Tối ưu cho Upsert: Tìm tất cả jobTitles tồn tại trong 1 câu query.
//     */
//    List<JobTitle> findByJobTitleCodeIn(Collection<String> jobTitleCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 jobTitle bằng jobTitleCode
//     */
//    Optional<JobTitle> findByJobTitleCode(String jobTitleCode);
//
//    Long countByJobTitleIdIn(Collection<Long> jobTitleIds);
}
