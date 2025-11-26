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

    Optional<EmployeeDecision> findByEmployee_IdAndDepartment_DepartmentIdAndEmployeeType_EmployeeTypeIdAndJobPosition_JobPositionIdAndJobTitle_JobTitleIdAndJobRank_JobRankIdAndCostCategoryLevel1_ExpenseTypeId(Long employeeId, Long departmentId, Long employeeTypeId, Long jobPositionId, Long jobTitleId, Long jobRankId, Long costCategoryLevel1);

    // Count methods for cascade delete checks
    long countByDepartment_DepartmentId(Long departmentId);

    long countByEmployeeType_EmployeeTypeId(Long employeeTypeId);

    long countByJobPosition_JobPositionId(Long jobPositionId);

    long countByJobTitle_JobTitleId(Long jobTitleId);

    long countByJobRank_JobRankId(Long jobRankId);

    long countByCostCategoryLevel1_ExpenseTypeId(Long expenseTypeId);

    long countByCostCategoryLevel2_ExpenseTypeId(Long expenseTypeId);

    long countByDecisionType_DecisionTypeId(Long decisionTypeId);
}

