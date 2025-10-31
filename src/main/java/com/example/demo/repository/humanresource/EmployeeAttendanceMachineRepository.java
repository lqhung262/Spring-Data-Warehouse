package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeAttendanceMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeAttendanceMachineRepository extends JpaRepository<EmployeeAttendanceMachine, Long> {
}
