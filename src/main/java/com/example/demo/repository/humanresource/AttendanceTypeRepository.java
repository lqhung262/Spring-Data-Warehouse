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

    Optional<AttendanceType> findByAttendanceTypeCode(String code);

    Optional<AttendanceType> findByName(String name);

    // Batch queries for bulk upsert
    List<AttendanceType> findBySourceIdIn(Collection<String> sourceIds);

    List<AttendanceType> findByNameIn(Collection<String> names);

    List<AttendanceType> findByAttendanceTypeCodeIn(Collection<String> codes);

}
