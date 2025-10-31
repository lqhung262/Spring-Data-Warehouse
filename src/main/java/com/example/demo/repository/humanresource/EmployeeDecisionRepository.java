package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDecisionRepository extends JpaRepository<EmployeeDecision, Long> {
}
