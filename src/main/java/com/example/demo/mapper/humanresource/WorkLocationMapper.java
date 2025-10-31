package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.WorkLocation.WorkLocationRequest;
import com.example.demo.dto.humanresource.WorkLocation.WorkLocationResponse;
import com.example.demo.entity.humanresource.WorkLocation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkLocationMapper {
    WorkLocation toWorkLocation(WorkLocationRequest request);

    WorkLocationResponse toWorkLocationResponse(WorkLocation workLocation);

    void updateWorkLocation(@MappingTarget WorkLocation workLocation, WorkLocationRequest request);
}
