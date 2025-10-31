package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.OldWard.OldWardRequest;
import com.example.demo.dto.humanresource.OldWard.OldWardResponse;
import com.example.demo.entity.humanresource.OldWard;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OldWardMapper {
    OldWard toOldWard(OldWardRequest request);

    OldWardResponse toOldWardResponse(OldWard oldWard);

    void updateOldWard(@MappingTarget OldWard oldWard, OldWardRequest request);
}