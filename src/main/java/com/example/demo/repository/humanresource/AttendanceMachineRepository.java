package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.AttendanceMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceMachineRepository extends JpaRepository<AttendanceMachine, Long> {
}
