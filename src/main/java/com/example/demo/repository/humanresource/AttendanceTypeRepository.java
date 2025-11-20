package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.AttendanceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceTypeRepository extends JpaRepository<AttendanceType, Long> {
    Optional<AttendanceType> findBySourceId(String sourceId);

//    /**
//     * Tối ưu cho Upsert: Tìm tất cả attendanceTypes tồn tại trong 1 câu query.
//     */
//    List<AttendanceType> findByAttendanceTypeCodeIn(Collection<String> attendanceTypeCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 attendanceType bằng attendanceTypeCode
//     */
//    Optional<AttendanceType> findByAttendanceTypeCode(String attendanceTypeCode);
//
//    Long countByAttendanceTypeIdIn(Collection<Long> attendanceTypeIds);
}
