package com.example.demo.mapper.humanresource;


import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionResponse;
import com.example.demo.entity.humanresource.EmployeeDecision;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeDecisionMapper {
    EmployeeDecision toEmployeeDecision(EmployeeDecisionRequest request);

    EmployeeDecisionResponse toEmployeeDecisionResponse(EmployeeDecision employeeDecision);

    void updateEmployeeDecision(@MappingTarget EmployeeDecision decision, EmployeeDecisionRequest request);
}
