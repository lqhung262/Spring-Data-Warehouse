package com.example.demo.mapper.general;

import com.example.demo.dto.general.Country.CountryRequest;
import com.example.demo.dto.general.Country.CountryResponse;
import com.example.demo.entity.general.Country;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CountryMapper {
    Country toCountry(CountryRequest request);

    CountryResponse toCountryResponse(Country country);

    void updateCountry(@MappingTarget Country country, CountryRequest request);
}
