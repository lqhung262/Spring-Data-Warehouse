package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findBySourceId(String sourceId);

    Optional<Department> findByDepartmentCode(String code);

    Optional<Department> findByName(String name);

    // Batch queries for bulk upsert
    List<Department> findBySourceIdIn(Collection<String> sourceIds);

    List<Department> findByNameIn(Collection<String> names);

    List<Department> findByDepartmentCodeIn(Collection<String> codes);
}
