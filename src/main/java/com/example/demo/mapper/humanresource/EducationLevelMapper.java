package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.EducationLevel.EducationLevelRequest;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelResponse;
import com.example.demo.entity.humanresource.EducationLevel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EducationLevelMapper {
    EducationLevel toEducationLevel(EducationLevelRequest request);

    EducationLevelResponse toEducationLevelResponse(EducationLevel educationLevel);

    void updateEducationLevel(@MappingTarget EducationLevel educationLevel, EducationLevelRequest request);
}