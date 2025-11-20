package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.WorkShiftGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkShiftGroupRepository extends JpaRepository<WorkShiftGroup, Long> {
    Optional<WorkShiftGroup> findBySourceId(String sourceId);

//    /**
//     * Tối ưu cho Upsert: Tìm tất cả workShiftGroups tồn tại trong 1 câu query.
//     */
//    List<WorkShiftGroup> findByWorkShiftGroupCodeIn(Collection<String> workShiftGroupCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 workShiftGroup bằng workShiftGroupCode
//     */
//    Optional<WorkShiftGroup> findByWorkShiftGroupCode(String workShiftGroupCode);
//
//    Long countByWorkShiftGroupIdIn(Collection<Long> workShiftGroupIds);
}
