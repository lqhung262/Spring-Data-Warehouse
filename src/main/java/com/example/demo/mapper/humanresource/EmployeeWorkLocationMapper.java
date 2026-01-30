package com.example.demo.mapper.humanresource;

import com.example.demo.dto.common.RefDto;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationResponse;
import com.example.demo.entity.humanresource.*;
import com.example.demo.mapper.common.CommonMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for EmployeeWorkLocation entity.
 * Uses CommonMapperConfig for shared settings.
 */
@Mapper(config = CommonMapperConfig.class)
public interface EmployeeWorkLocationMapper {

    EmployeeWorkLocation toEmployeeWorkLocation(EmployeeWorkLocationRequest request);

    @Mapping(target = "id", source = "employeeWorkLocationId")
    @Mapping(target = "workLocation", expression = "java(toWorkLocationRef(ewl.getWorkLocation()))")
    EmployeeWorkLocationResponse toEmployeeWorkLocationResponse(EmployeeWorkLocation ewl);

    void updateEmployeeWorkLocation(@MappingTarget EmployeeWorkLocation employeeWorkLocation, EmployeeWorkLocationRequest request);

    default RefDto toWorkLocationRef(WorkLocation entity) {
        return entity == null ? null : RefDto.of(entity.getWorkLocationId(), entity.getName());
    }

    default void setReferences(EmployeeWorkLocation employeeWorkLocation, EmployeeWorkLocationRequest request) {
        if (request.getWorkLocationId() != null) {
            WorkLocation workLocation = new WorkLocation();
            workLocation.setWorkLocationId(request.getWorkLocationId());
            employeeWorkLocation.setWorkLocation(workLocation);
        }
    }
}
