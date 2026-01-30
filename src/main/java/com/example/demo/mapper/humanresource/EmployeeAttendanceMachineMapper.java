package com.example.demo.mapper.humanresource;

import com.example.demo.dto.common.RefDto;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineResponse;
import com.example.demo.entity.humanresource.*;
import com.example.demo.mapper.common.CommonMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for EmployeeAttendanceMachine entity.
 * Uses CommonMapperConfig for shared settings.
 */
@Mapper(config = CommonMapperConfig.class)
public interface EmployeeAttendanceMachineMapper {

    EmployeeAttendanceMachine toEmployeeAttendanceMachine(EmployeeAttendanceMachineRequest request);

    @Mapping(target = "id", source = "employeeAttendanceMachineId")
    @Mapping(target = "machine", expression = "java(toMachineRef(eam.getMachine()))")
    EmployeeAttendanceMachineResponse toEmployeeAttendanceMachineResponse(EmployeeAttendanceMachine eam);

    void updateEmployeeAttendanceMachine(@MappingTarget EmployeeAttendanceMachine employeeAttendanceMachine, EmployeeAttendanceMachineRequest request);

    default RefDto toMachineRef(AttendanceMachine entity) {
        return entity == null ? null : RefDto.of(entity.getAttendanceMachineId(), entity.getName());
    }

    default void setReferences(EmployeeAttendanceMachine employeeAttendanceMachine, EmployeeAttendanceMachineRequest request) {
        if (request.getMachineId() != null) {
            AttendanceMachine machine = new AttendanceMachine();
            machine.setAttendanceMachineId(request.getMachineId());
            employeeAttendanceMachine.setMachine(machine);
        }
    }
}
