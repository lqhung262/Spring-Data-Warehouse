package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityRequest;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OldProvinceCityMapper {

    @Mapping(target = "oldProvinceCityId", ignore = true)
    @Mapping(target = "provinceCity", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    OldProvinceCity toOldProvinceCity(OldProvinceCityRequest request);

    @Mapping(target = "provinceCityId", source = "provinceCity.provinceCityId")
    OldProvinceCityResponse toOldProvinceCityResponse(OldProvinceCity oldProvinceCity);

    @Mapping(target = "oldProvinceCityId", ignore = true)
    @Mapping(target = "provinceCity", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateOldProvinceCity(@MappingTarget OldProvinceCity oldProvinceCity, OldProvinceCityRequest request);

    default void setReferences(OldProvinceCity oldProvinceCity, OldProvinceCityRequest request) {
        if (request.getProvinceCityId() != null) {
            ProvinceCity provinceCity = new ProvinceCity();
            provinceCity.setProvinceCityId(request.getProvinceCityId());
            oldProvinceCity.setProvinceCity(provinceCity);
        }
    }
}