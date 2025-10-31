package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeRequest;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeResponse;
import com.example.demo.entity.humanresource.EmployeeType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeTypeMapper {
    EmployeeType toEmployeeType(EmployeeTypeRequest request);

    EmployeeTypeResponse toEmployeeTypeResponse(EmployeeType employeeType);

    void updateEmployeeType(@MappingTarget EmployeeType employeeType, EmployeeTypeRequest request);
}
