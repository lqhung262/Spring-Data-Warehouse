package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineResponse;
import com.example.demo.entity.humanresource.EmployeeAttendanceMachine;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeAttendanceMachineMapper {
    EmployeeAttendanceMachine toEmployeeAttendanceMachine(EmployeeAttendanceMachineRequest request);

    EmployeeAttendanceMachineResponse toEmployeeAttendanceMachineResponse(EmployeeAttendanceMachine employeeAttendanceMachine);

    void updateEmployeeAttendanceMachine(@MappingTarget EmployeeAttendanceMachine employeeAttendanceMachine, EmployeeAttendanceMachineRequest request);
}
