package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeAttendanceMachineMapper {

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "employeeAttendanceMachineId", ignore = true)
    @Mapping(target = "machine", ignore = true)
    EmployeeAttendanceMachine toEmployeeAttendanceMachine(EmployeeAttendanceMachineRequest request);

    @Mapping(target = "machineId", source = "machine.attendanceMachineId")
    EmployeeAttendanceMachineResponse toEmployeeAttendanceMachineResponse(EmployeeAttendanceMachine employeeAttendanceMachine);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "employeeAttendanceMachineId", ignore = true)
    @Mapping(target = "machine", ignore = true)
    void updateEmployeeAttendanceMachine(@MappingTarget EmployeeAttendanceMachine employeeAttendanceMachine, EmployeeAttendanceMachineRequest request);

    default void setReferences(EmployeeAttendanceMachine employeeAttendanceMachine, EmployeeAttendanceMachineRequest request) {
        if (request.getMachineId() != null) {
            AttendanceMachine machine = new AttendanceMachine();
            machine.setAttendanceMachineId(request.getMachineId());
            employeeAttendanceMachine.setMachine(machine);
        }
    }
}
