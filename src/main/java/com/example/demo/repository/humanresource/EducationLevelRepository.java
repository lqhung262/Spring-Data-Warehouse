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
//    /**
//     * Tối ưu cho Upsert: Tìm tất cả educationLevels tồn tại trong 1 câu query.
//     */
//    List<EducationLevel> findByEducationLevelCodeIn(Collection<String> educationLevelCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 educationLevel bằng educationLevelCode
//     */
//    Optional<EducationLevel> findByEducationLevelCode(String educationLevelCode);
//
//    Long countByEducationLevelIdIn(Collection<Long> educationLevelIds);
}
