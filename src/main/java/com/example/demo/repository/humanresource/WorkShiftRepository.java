package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {
    Optional<WorkShift> findBySourceId(String sourceId);

//    /**
//     * Tối ưu cho Upsert: Tìm tất cả workShifts tồn tại trong 1 câu query.
//     */
//    List<WorkShift> findByWorkShiftCodeIn(Collection<String> workShiftCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 workShift bằng workShiftCode
//     */
//    Optional<WorkShift> findByWorkShiftCode(String workShiftCode);
//
//    Long countByWorkShiftIdIn(Collection<Long> workShiftIds);
}
