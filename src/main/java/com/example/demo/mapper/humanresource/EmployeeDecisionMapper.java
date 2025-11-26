package com.example.demo.mapper.humanresource;


import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeDecisionMapper {

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "employeeDecisionId", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "employeeType", ignore = true)
    @Mapping(target = "jobPosition", ignore = true)
    @Mapping(target = "jobTitle", ignore = true)
    @Mapping(target = "jobRank", ignore = true)
    @Mapping(target = "decisionType", ignore = true)
    @Mapping(target = "costCategoryLevel1", ignore = true)
    @Mapping(target = "costCategoryLevel2", ignore = true)
    EmployeeDecision toEmployeeDecision(EmployeeDecisionRequest request);

    @Mapping(target = "departmentId", source = "department.departmentId")
    @Mapping(target = "employeeTypeId", source = "employeeType.employeeTypeId")
    @Mapping(target = "jobPositionId", source = "jobPosition.jobPositionId")
    @Mapping(target = "jobTitleId", source = "jobTitle.jobTitleId")
    @Mapping(target = "jobRankId", source = "jobRank.jobRankId")
    @Mapping(target = "decisionTypeId", source = "decisionType.decisionTypeId")
    @Mapping(target = "costCategoryLevel1", source = "costCategoryLevel1.expenseTypeId")
    @Mapping(target = "costCategoryLevel2", source = "costCategoryLevel2.expenseTypeId")
    EmployeeDecisionResponse toEmployeeDecisionResponse(EmployeeDecision employeeDecision);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "employeeDecisionId", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "employeeType", ignore = true)
    @Mapping(target = "jobPosition", ignore = true)
    @Mapping(target = "jobTitle", ignore = true)
    @Mapping(target = "jobRank", ignore = true)
    @Mapping(target = "decisionType", ignore = true)
    @Mapping(target = "costCategoryLevel1", ignore = true)
    @Mapping(target = "costCategoryLevel2", ignore = true)
    void updateEmployeeDecision(@MappingTarget EmployeeDecision decision, EmployeeDecisionRequest request);

    // Helper methods to set references from IDs - to be called manually in service layer
    default void setReferences(EmployeeDecision decision, EmployeeDecisionRequest request) {
        if (request.getDepartmentId() != null) {
            Department department = new Department();
            department.setDepartmentId(request.getDepartmentId());
            decision.setDepartment(department);
        }
        if (request.getEmployeeTypeId() != null) {
            EmployeeType employeeType = new EmployeeType();
            employeeType.setEmployeeTypeId(request.getEmployeeTypeId());
            decision.setEmployeeType(employeeType);
        }
        if (request.getJobPositionId() != null) {
            JobPosition jobPosition = new JobPosition();
            jobPosition.setJobPositionId(request.getJobPositionId());
            decision.setJobPosition(jobPosition);
        }
        if (request.getJobTitleId() != null) {
            JobTitle jobTitle = new JobTitle();
            jobTitle.setJobTitleId(request.getJobTitleId());
            decision.setJobTitle(jobTitle);
        }
        if (request.getJobRankId() != null) {
            JobRank jobRank = new JobRank();
            jobRank.setJobRankId(request.getJobRankId());
            decision.setJobRank(jobRank);
        }
        if (request.getDecisionTypeId() != null) {
            DecisionType decisionType = new DecisionType();
            decisionType.setDecisionTypeId(request.getDecisionTypeId());
            decision.setDecisionType(decisionType);
        }
        if (request.getCostCategoryLevel1() != null) {
            ExpenseType expenseType1 = new ExpenseType();
            expenseType1.setExpenseTypeId(request.getCostCategoryLevel1());
            decision.setCostCategoryLevel1(expenseType1);
        }
        if (request.getCostCategoryLevel2() != null) {
            ExpenseType expenseType2 = new ExpenseType();
            expenseType2.setExpenseTypeId(request.getCostCategoryLevel2());
            decision.setCostCategoryLevel2(expenseType2);
        }
    }
}
