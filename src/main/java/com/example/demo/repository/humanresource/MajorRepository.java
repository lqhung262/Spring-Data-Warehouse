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

//    /**
//     * Tối ưu cho Upsert: Tìm tất cả majors tồn tại trong 1 câu query.
//     */
//    List<Major> findByMajorCodeIn(Collection<String> majorCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 major bằng majorCode
//     */
//    Optional<Major> findByMajorCode(String majorCode);
//
//    Long countByMajorIdIn(Collection<Long> majorIds);
}
