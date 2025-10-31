package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Ward.WardRequest;
import com.example.demo.dto.humanresource.Ward.WardResponse;
import com.example.demo.entity.humanresource.Ward;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WardMapper {
    Ward toWard(WardRequest request);

    WardResponse toWardResponse(Ward ward);

    void updateWard(@MappingTarget Ward ward, WardRequest request);
}