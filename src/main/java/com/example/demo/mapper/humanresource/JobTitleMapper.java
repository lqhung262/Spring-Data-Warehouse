package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.JobTitle.JobTitleRequest;
import com.example.demo.dto.humanresource.JobTitle.JobTitleResponse;
import com.example.demo.entity.humanresource.JobTitle;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobTitleMapper {
    JobTitle toJobTitle(JobTitleRequest request);

    JobTitleResponse toJobTitleResponse(JobTitle jobTitle);

    void updateJobTitle(@MappingTarget JobTitle jobTitle, JobTitleRequest request);
}