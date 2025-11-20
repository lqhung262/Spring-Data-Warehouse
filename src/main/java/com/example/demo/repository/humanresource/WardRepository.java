package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WardRepository extends JpaRepository<Ward, Long> {
    Optional<Ward> findBySourceId(String sourceId);
//    /**
//     * Tối ưu cho Upsert: Tìm tất cả wards tồn tại trong 1 câu query.
//     */
////    List<Ward> findByWardCodeIn(Collection<String> wardCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 ward bằng wardId
//     */
//    Optional<Ward> findByWardId(Long wardId);
//
//    Long countByWardIdIn(Collection<Long> wardIds);
}
