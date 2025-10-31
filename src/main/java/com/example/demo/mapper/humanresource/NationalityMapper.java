package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.Nationality.NationalityRequest;
import com.example.demo.dto.humanresource.Nationality.NationalityResponse;
import com.example.demo.entity.humanresource.Nationality;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface NationalityMapper {
    Nationality toNationality(NationalityRequest request);

    NationalityResponse toNationalityResponse(Nationality nationality);

    void updateNationality(@MappingTarget Nationality nationality, NationalityRequest request);
}