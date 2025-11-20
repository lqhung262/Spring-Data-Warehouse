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

//    /**
//     * Tối ưu cho Upsert: Tìm tất cả departments tồn tại trong 1 câu query.
//     */
//    List<Department> findByDepartmentCodeIn(Collection<String> departmentCodes);
//
//    /**
//     * Dùng cho Upsert: Tìm 1 department bằng departmentCode
//     */
//    Optional<Department> findByDepartmentCode(String departmentCode);
//
//    Long countByDepartmentIdIn(Collection<Long> departmentIds);
}
