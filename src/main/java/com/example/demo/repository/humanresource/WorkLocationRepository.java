package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.WorkLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkLocationRepository extends JpaRepository<WorkLocation, Long> {
//    /**
//     * Tối ưu cho Upsert: Tìm tất cả workLocations tồn tại trong 1 câu query.
//     */
//    List<WorkLocation> findByWorkLocationCodeIn(Collection<String> workLocationCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 workLocation bằng workLocationCode
//     */
//    Optional<WorkLocation> findByWorkLocationCode(String workLocationCode);
//
//    Long countByWorkLocationIdIn(Collection<Long> workLocationIds);
}
