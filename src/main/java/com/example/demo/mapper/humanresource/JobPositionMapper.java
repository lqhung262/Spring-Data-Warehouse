package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.JobPosition.JobPositionRequest;
import com.example.demo.dto.humanresource.JobPosition.JobPositionResponse;
import com.example.demo.entity.humanresource.JobPosition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobPositionMapper {
    JobPosition toJobPosition(JobPositionRequest request);

    JobPositionResponse toJobPositionResponse(JobPosition jobPosition);

    void updateJobPosition(@MappingTarget JobPosition jobPosition, JobPositionRequest request);
}