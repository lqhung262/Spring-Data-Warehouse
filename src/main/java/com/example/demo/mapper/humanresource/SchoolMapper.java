package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.School.SchoolRequest;
import com.example.demo.dto.humanresource.School.SchoolResponse;
import com.example.demo.entity.humanresource.School;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SchoolMapper {
    School toSchool(SchoolRequest request);

    SchoolResponse toSchoolResponse(School school);

    void updateSchool(@MappingTarget School school, SchoolRequest request);
}