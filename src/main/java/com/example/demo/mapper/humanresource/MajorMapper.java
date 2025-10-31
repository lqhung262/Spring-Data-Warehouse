package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Major.MajorRequest;
import com.example.demo.dto.humanresource.Major.MajorResponse;
import com.example.demo.entity.humanresource.Major;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MajorMapper {
    Major toMajor(MajorRequest request);

    MajorResponse toMajorResponse(Major major);

    void updateMajor(@MappingTarget Major major, MajorRequest request);
}