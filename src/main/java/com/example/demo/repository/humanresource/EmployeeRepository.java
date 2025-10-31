package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
//    boolean ExistsEmployeeByFullName(String fullName);
}
