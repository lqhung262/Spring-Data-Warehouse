package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityRequest;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityResponse;
import com.example.demo.entity.humanresource.MedicalFacility;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MedicalFacilityMapper {
    MedicalFacility toMedicalFacility(MedicalFacilityRequest request);

    MedicalFacilityResponse toMedicalFacilityResponse(MedicalFacility medicalFacility);

    void updateMedicalFacility(@MappingTarget MedicalFacility medicalFacility, MedicalFacilityRequest request);
}