package com.example.demo.repository.humanresource;

import com.example.demo.entity.humanresource.EmployeeDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeDecisionRepository extends JpaRepository<EmployeeDecision, Long> {
    Optional<EmployeeDecision> findByDecisionNo(String decisionNo);

    List<EmployeeDecision> findByEmployee_Id(Long employeeId);

    Optional<EmployeeDecision> findByEmployee_IdAndDepartmentIdAndEmployeeTypeIdAndJobPositionIdAndJobTitleIdAndJobRankIdAndCostCategoryLevel1(Long employeeId, Long departmentId, Long employeeTypeId, Long jobPositionId, Long jobTitleId, Long jobRankId, Long costCategoryLevel1);
}
