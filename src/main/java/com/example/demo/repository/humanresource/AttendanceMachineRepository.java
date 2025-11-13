package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.AttendanceMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceMachineRepository extends JpaRepository<AttendanceMachine, Long> {
    List<AttendanceMachine> findByAttendanceMachineCodeIn(Collection<String> attendanceMachineCodes);

    /**
     * Dùng cho Upsert: Tìm 1 attendanceMachine bằng attendanceMachineCode
     */
    Optional<AttendanceMachine> findByAttendanceMachineCode(String attendanceMachineCode);

    Long countByAttendanceMachineIdIn(Collection<Long> attendanceMachineIds);
}
