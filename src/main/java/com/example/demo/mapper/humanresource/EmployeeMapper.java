package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeResponse;
import com.example.demo.entity.humanresource.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {EmployeeDecisionMapper.class, EmployeeEducationMapper.class, EmployeeWorkShiftMapper.class, EmployeeAttendanceMachineMapper.class, EmployeeWorkLocationMapper.class})
public interface EmployeeMapper {
    @Mapping(target = "employeeDecisionList", ignore = true)
    @Mapping(target = "employeeEducationList", ignore = true)
    @Mapping(target = "employeeAttendanceMachineList", ignore = true)
    @Mapping(target = "employeeWorkLocationList", ignore = true)
    @Mapping(target = "employeeWorkShift", ignore = true)
    Employee toEmployee(EmployeeRequest request);

    @Mapping(source = "employeeDecisionList", target = "employeeDecisions")
    @Mapping(source = "employeeEducationList", target = "employeeEducations")
    @Mapping(source = "employeeWorkShift", target = "employeeWorkShift")
    @Mapping(source = "employeeAttendanceMachineList", target = "employeeAttendanceMachines")
    @Mapping(source = "employeeWorkLocationList", target = "employeeWorkLocations")
    EmployeeResponse toEmployeeResponse(Employee employee);

    @Mapping(target = "employeeDecisionList", ignore = true)
    @Mapping(target = "employeeEducationList", ignore = true)
    @Mapping(target = "employeeAttendanceMachineList", ignore = true)
    @Mapping(target = "employeeWorkLocationList", ignore = true)
    @Mapping(target = "employeeWorkShift", ignore = true)
    void updateEmployee(@MappingTarget Employee employee, EmployeeRequest request);
}
