package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Ward.WardRequest;
import com.example.demo.dto.humanresource.Ward.WardResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WardMapper {

    @Mapping(target = "wardId", ignore = true)
    @Mapping(target = "provinceCity", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    Ward toWard(WardRequest request);

    @Mapping(target = "provinceCityId", source = "provinceCity.provinceCityId")
    WardResponse toWardResponse(Ward ward);

    @Mapping(target = "wardId", ignore = true)
    @Mapping(target = "provinceCity", ignore = true)
    @Mapping(target = "sourceSystemId", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    void updateWard(@MappingTarget Ward ward, WardRequest request);

    default void setReferences(Ward ward, WardRequest request) {
        if (request.getProvinceCityId() != null) {
            ProvinceCity provinceCity = new ProvinceCity();
            provinceCity.setProvinceCityId(request.getProvinceCityId());
            ward.setProvinceCity(provinceCity);
        }
    }
}