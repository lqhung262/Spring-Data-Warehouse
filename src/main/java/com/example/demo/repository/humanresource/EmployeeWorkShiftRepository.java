package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeWorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeWorkShiftRepository extends JpaRepository<EmployeeWorkShift, Long> {
}
