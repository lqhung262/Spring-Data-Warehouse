package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityRequest;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityResponse;
import com.example.demo.entity.humanresource.OldProvinceCity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OldProvinceCityMapper {
    OldProvinceCity toOldProvinceCity(OldProvinceCityRequest request);

    OldProvinceCityResponse toOldProvinceCityResponse(OldProvinceCity oldProvinceCity);

    void updateOldProvinceCity(@MappingTarget OldProvinceCity oldProvinceCity, OldProvinceCityRequest request);
}