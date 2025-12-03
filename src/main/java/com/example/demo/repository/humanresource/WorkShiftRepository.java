package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {
    Optional<WorkShift> findBySourceId(String sourceId);

    Optional<WorkShift> findByWorkShiftCode(String code);

    Optional<WorkShift> findByName(String name);

    // Batch queries for bulk upsert
    List<WorkShift> findBySourceIdIn(Collection<String> sourceIds);

    List<WorkShift> findByNameIn(Collection<String> names);

    List<WorkShift> findByWorkShiftCodeIn(Collection<String> codes);
}
