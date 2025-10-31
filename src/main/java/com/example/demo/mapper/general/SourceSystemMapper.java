package com.example.demo.mapper.general;

import com.example.demo.dto.general.SourceSystem.SourceSystemRequest;
import com.example.demo.dto.general.SourceSystem.SourceSystemResponse;
import com.example.demo.entity.general.SourceSystem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SourceSystemMapper {
    SourceSystem toSourceSystem(SourceSystemRequest request);

    SourceSystemResponse toSourceSystemResponse(SourceSystem sourceSystem);

    void updateSourceSystem(@MappingTarget SourceSystem sourceSystem, SourceSystemRequest request);
}
