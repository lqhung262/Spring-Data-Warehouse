package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.AttendanceMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceMachineRepository extends JpaRepository<AttendanceMachine, Long> {
    Optional<AttendanceMachine> findBySourceId(String sourceId);

    Optional<AttendanceMachine> findByAttendanceMachineCode(String code);

    Optional<AttendanceMachine> findByName(String name);

    // Batch queries for bulk upsert
    List<AttendanceMachine> findBySourceIdIn(Collection<String> sourceIds);

    List<AttendanceMachine> findByNameIn(Collection<String> names);

    List<AttendanceMachine> findByAttendanceMachineCodeIn(Collection<String> codes);

}
