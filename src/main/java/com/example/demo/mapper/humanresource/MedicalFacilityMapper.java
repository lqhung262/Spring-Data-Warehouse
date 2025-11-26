package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityRequest;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MedicalFacilityMapper {

    @Mapping(target = "medicalFacilityId", ignore = true)
    @Mapping(target = "provinceCity", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    MedicalFacility toMedicalFacility(MedicalFacilityRequest request);

    @Mapping(target = "provinceCityId", source = "provinceCity.provinceCityId")
    MedicalFacilityResponse toMedicalFacilityResponse(MedicalFacility medicalFacility);

    @Mapping(target = "medicalFacilityId", ignore = true)
    @Mapping(target = "provinceCity", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateMedicalFacility(@MappingTarget MedicalFacility medicalFacility, MedicalFacilityRequest request);

    default void setReferences(MedicalFacility medicalFacility, MedicalFacilityRequest request) {
        if (request.getProvinceCityId() != null) {
            ProvinceCity provinceCity = new ProvinceCity();
            provinceCity.setProvinceCityId(request.getProvinceCityId());
            medicalFacility.setProvinceCity(provinceCity);
        }
    }
}