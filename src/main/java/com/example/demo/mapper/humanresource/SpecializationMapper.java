package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Specialization.SpecializationRequest;
import com.example.demo.dto.humanresource.Specialization.SpecializationResponse;
import com.example.demo.entity.humanresource.Specialization;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SpecializationMapper {
    Specialization toSpecialization(SpecializationRequest request);

    SpecializationResponse toSpecializationResponse(Specialization specialization);

    void updateSpecialization(@MappingTarget Specialization specialization, SpecializationRequest request);
}