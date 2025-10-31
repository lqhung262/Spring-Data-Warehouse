package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftResponse;
import com.example.demo.entity.humanresource.EmployeeWorkShift;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeWorkShiftMapper {
    EmployeeWorkShift toEmployeeWorkShift(EmployeeWorkShiftRequest request);

    EmployeeWorkShiftResponse toEmployeeWorkShiftResponse(EmployeeWorkShift employeeWorkShift);

    void updateEmployeeWorkShift(@MappingTarget EmployeeWorkShift employeeWorkShift, EmployeeWorkShiftRequest request);
}
