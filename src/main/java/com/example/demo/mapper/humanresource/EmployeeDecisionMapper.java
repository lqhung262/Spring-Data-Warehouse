package com.example.demo.mapper.humanresource;


import com.example.demo.dto.common.RefDto;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionResponse;
import com.example.demo.entity.humanresource.*;
import com.example.demo.mapper.common.CommonMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for EmployeeDecision entity.
 * Uses CommonMapperConfig for shared settings (no need for explicit ignore mappings).
 */
@Mapper(config = CommonMapperConfig.class)
public interface EmployeeDecisionMapper {

    @Mapping(target = "costCategoryLevel1", ignore = true)
    @Mapping(target = "costCategoryLevel2", ignore = true)
    EmployeeDecision toEmployeeDecision(EmployeeDecisionRequest request);

    @Mapping(target = "id", source = "employeeDecisionId")
    @Mapping(target = "department", expression = "java(toDepartmentRef(decision.getDepartment()))")
    @Mapping(target = "employeeType", expression = "java(toEmployeeTypeRef(decision.getEmployeeType()))")
    @Mapping(target = "jobPosition", expression = "java(toJobPositionRef(decision.getJobPosition()))")
    @Mapping(target = "jobTitle", expression = "java(toJobTitleRef(decision.getJobTitle()))")
    @Mapping(target = "jobRank", expression = "java(toJobRankRef(decision.getJobRank()))")
    @Mapping(target = "decisionType", expression = "java(toDecisionTypeRef(decision.getDecisionType()))")
    @Mapping(target = "costCategoryLevel1", expression = "java(toExpenseTypeRef(decision.getCostCategoryLevel1()))")
    @Mapping(target = "costCategoryLevel2", expression = "java(toExpenseTypeRef(decision.getCostCategoryLevel2()))")
    EmployeeDecisionResponse toEmployeeDecisionResponse(EmployeeDecision decision);

    @Mapping(target = "costCategoryLevel1", ignore = true)
    @Mapping(target = "costCategoryLevel2", ignore = true)
    void updateEmployeeDecision(@MappingTarget EmployeeDecision decision, EmployeeDecisionRequest request);

    // Helper methods to convert entities to RefDto
    default RefDto toDepartmentRef(Department entity) {
        return entity == null ? null : RefDto.of(entity.getDepartmentId(), entity.getName());
    }
    
    default RefDto toEmployeeTypeRef(EmployeeType entity) {
        return entity == null ? null : RefDto.of(entity.getEmployeeTypeId(), entity.getName());
    }
    
    default RefDto toJobPositionRef(JobPosition entity) {
        return entity == null ? null : RefDto.of(entity.getJobPositionId(), entity.getName());
    }
    
    default RefDto toJobTitleRef(JobTitle entity) {
        return entity == null ? null : RefDto.of(entity.getJobTitleId(), entity.getName());
    }
    
    default RefDto toJobRankRef(JobRank entity) {
        return entity == null ? null : RefDto.of(entity.getJobRankId(), entity.getName());
    }
    
    default RefDto toDecisionTypeRef(DecisionType entity) {
        return entity == null ? null : RefDto.of(entity.getDecisionTypeId(), entity.getName());
    }
    
    default RefDto toExpenseTypeRef(ExpenseType entity) {
        return entity == null ? null : RefDto.of(entity.getExpenseTypeId(), entity.getName());
    }

    // Helper methods to set references from IDs
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
