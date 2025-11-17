package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeAttendanceMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeAttendanceMachineRepository extends JpaRepository<EmployeeAttendanceMachine, Long> {
    Optional<EmployeeAttendanceMachine> findByEmployee_IdAndMachineId(Long employeeId, Long machineId);

    List<EmployeeAttendanceMachine> findByEmployee_Id(Long employeeId);
}
