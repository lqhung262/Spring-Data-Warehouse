package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeTypeRepository extends JpaRepository<EmployeeType, Long> {
    Optional<EmployeeType> findBySourceId(String sourceId);

    Optional<EmployeeType> findByEmployeeTypeCode(String code);

    Optional<EmployeeType> findByName(String name);

    // Batch queries for bulk upsert
    List<EmployeeType> findBySourceIdIn(Collection<String> sourceIds);

    List<EmployeeType> findByNameIn(Collection<String> names);

    List<EmployeeType> findByEmployeeTypeCodeIn(Collection<String> codes);
}
