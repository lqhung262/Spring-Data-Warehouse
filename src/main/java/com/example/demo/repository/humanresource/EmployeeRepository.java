package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findBySourceId(String sourceId);

    List<Employee> findByIdIn(Collection<Long> ids);

//    /**
//     * Tối ưu cho Upsert: Tìm tất cả employeess tồn tại trong 1 câu query.
//     */
//    List<Employee> findByEmployeeCodeIn(Collection<String> employeesCodes);

    /**
     * Dùng cho Upsert: Tìm 1 employees bằng employeesCode
     */
//    Optional<Employee> findByEmployeeCode(String employeesCode);

////    Long countByEmployeeIdIn(Collection<Long> employeesIds);
}
