package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeWorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeWorkShiftRepository extends JpaRepository<EmployeeWorkShift, Long> {
    List<EmployeeWorkShift> findByEmployee_Id(Long employeeId);

    Optional<EmployeeWorkShift> findByEmployee_IdAndAttendanceType_AttendanceTypeIdAndWorkShift_WorkShiftIdAndOtType_OtTypeIdAndWorkShiftGroup_WorkShiftGroupId(
            Long employeeId,
            Long attendanceTypeId,
            Long workShiftId,
            Long otTypeId,
            Long workShiftGroupId
    );

    // Count methods for cascade delete checks
    long countByWorkShift_WorkShiftId(Long workShiftId);

    long countByWorkShiftGroup_WorkShiftGroupId(Long workShiftGroupId);

    long countByAttendanceType_AttendanceTypeId(Long attendanceTypeId);

    long countByOtType_OtTypeId(Long otTypeId);
}
