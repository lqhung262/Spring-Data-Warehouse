package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationResponse;
import com.example.demo.entity.humanresource.EmployeeWorkLocation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeWorkLocationMapper {
    EmployeeWorkLocation toEmployeeWorkLocation(EmployeeWorkLocationRequest request);

    EmployeeWorkLocationResponse toEmployeeWorkLocationResponse(EmployeeWorkLocation employeeWorkLocation);

    void updateEmployeeWorkLocation(@MappingTarget EmployeeWorkLocation employeeWorkLocation, EmployeeWorkLocationRequest request);
}
