package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeAttendanceMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeAttendanceMachineRepository extends JpaRepository<EmployeeAttendanceMachine, Long> {
    /**
     * Tối ưu cho Upsert: Tìm tất cả employeeAttendanceMachines tồn tại trong 1 câu query.
     */
    List<EmployeeAttendanceMachine> findByEmployeeAttendanceMachineCodeIn(Collection<Long> employeeAttendanceMachineCodes);

    /**
     * Dùng cho Upsert: Tìm 1 employeeAttendanceMachine bằng employeeAttendanceMachineId
     */
    Optional<EmployeeAttendanceMachine> findByEmployeeAttendanceMachineId(Long employeeAttendanceMachineId);

    Long countByEmployeeAttendanceMachineIdIn(Collection<Long> employeeAttendanceMachineIds);
}
