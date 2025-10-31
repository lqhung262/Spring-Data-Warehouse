package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityResponse;
import com.example.demo.entity.humanresource.ProvinceCity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProvinceCityMapper {
    ProvinceCity toProvinceCity(ProvinceCityRequest request);

    ProvinceCityResponse toProvinceCityResponse(ProvinceCity provinceCity);

    void updateProvinceCity(@MappingTarget ProvinceCity provinceCity, ProvinceCityRequest request);
}