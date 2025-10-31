package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.JobRank.JobRankRequest;
import com.example.demo.dto.humanresource.JobRank.JobRankResponse;
import com.example.demo.entity.humanresource.JobRank;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface JobRankMapper {
    JobRank toJobRank(JobRankRequest request);

    JobRankResponse toJobRankResponse(JobRank jobRank);

    void updateJobRank(@MappingTarget JobRank jobRank, JobRankRequest request);
}