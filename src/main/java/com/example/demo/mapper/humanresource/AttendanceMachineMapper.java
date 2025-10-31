package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineRequest;
import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineResponse;
import com.example.demo.entity.humanresource.AttendanceMachine;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AttendanceMachineMapper {
    AttendanceMachine toAttendanceMachine(AttendanceMachineRequest request);

    AttendanceMachineResponse toAttendanceMachineResponse(AttendanceMachine machine);

    void updateAttendanceMachine(@MappingTarget AttendanceMachine machine, AttendanceMachineRequest request);
}