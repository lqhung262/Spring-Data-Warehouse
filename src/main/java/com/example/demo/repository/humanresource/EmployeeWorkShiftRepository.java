package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeWorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeWorkShiftRepository extends JpaRepository<EmployeeWorkShift, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả employeeWorkShifts tồn tại trong 1 câu query.
     */
    List<EmployeeWorkShift> findByAttendanceCodeIn(Collection<String> employeeWorkShiftCodes);

    /**
     * Dùng cho Upsert: Tìm 1 employeeWorkShift bằng attendanceCode
     */
    Optional<EmployeeWorkShift> findByAttendanceCode(String attendanceCode);

    Long countByEmployeeWorkShiftIdIn(Collection<Long> employeeWorkShiftIds);
}
