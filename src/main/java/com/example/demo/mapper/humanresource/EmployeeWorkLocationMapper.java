package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeWorkLocationMapper {

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "employeeWorkLocationId", ignore = true)
    @Mapping(target = "workLocation", ignore = true)
    EmployeeWorkLocation toEmployeeWorkLocation(EmployeeWorkLocationRequest request);

    @Mapping(target = "workLocationId", source = "workLocation.workLocationId")
    EmployeeWorkLocationResponse toEmployeeWorkLocationResponse(EmployeeWorkLocation employeeWorkLocation);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "employeeWorkLocationId", ignore = true)
    @Mapping(target = "workLocation", ignore = true)
    void updateEmployeeWorkLocation(@MappingTarget EmployeeWorkLocation employeeWorkLocation, EmployeeWorkLocationRequest request);

    default void setReferences(EmployeeWorkLocation employeeWorkLocation, EmployeeWorkLocationRequest request) {
        if (request.getWorkLocationId() != null) {
            WorkLocation workLocation = new WorkLocation();
            workLocation.setWorkLocationId(request.getWorkLocationId());
            employeeWorkLocation.setWorkLocation(workLocation);
        }
    }
}
