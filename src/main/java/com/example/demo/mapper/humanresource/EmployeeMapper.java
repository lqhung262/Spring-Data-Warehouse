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
    Employee toEmployee(EmployeeRequest request);

    @Mapping(source = "employeeDecisionList", target = "employeeDecisions")
    @Mapping(source = "employeeEducationList", target = "employeeEducations")
    @Mapping(source = "employeeWorkShift", target = "employeeWorkShift")
    @Mapping(source = "employeeAttendanceMachineList", target = "employeeAttendanceMachines")
    @Mapping(source = "employeeWorkLocationList", target = "employeeWorkLocations")
    EmployeeResponse toEmployeeResponse(Employee employee);

    void updateEmployee(@MappingTarget Employee employee, EmployeeRequest request);
}
