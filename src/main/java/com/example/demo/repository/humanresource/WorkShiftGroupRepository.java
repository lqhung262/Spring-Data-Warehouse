package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.WorkShiftGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkShiftGroupRepository extends JpaRepository<WorkShiftGroup, Long> {
    Optional<WorkShiftGroup> findBySourceId(String sourceId);

    Optional<WorkShiftGroup> findByWorkShiftGroupCode(String code);

    Optional<WorkShiftGroup> findByName(String name);

    // Batch queries for bulk upsert
    List<WorkShiftGroup> findBySourceIdIn(Collection<String> sourceIds);

    List<WorkShiftGroup> findByNameIn(Collection<String> names);

    List<WorkShiftGroup> findByWorkShiftGroupCodeIn(Collection<String> codes);
}
