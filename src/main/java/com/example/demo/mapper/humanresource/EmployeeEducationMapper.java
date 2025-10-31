package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationResponse;
import com.example.demo.entity.humanresource.EmployeeEducation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeEducationMapper {
    EmployeeEducation toEmployeeEducation(EmployeeEducationRequest request);

    EmployeeEducationResponse toEmployeeEducationResponse(EmployeeEducation employeeEducation);

    void updateEmployeeEducation(@MappingTarget EmployeeEducation employeeEducation, EmployeeEducationRequest request);
}
