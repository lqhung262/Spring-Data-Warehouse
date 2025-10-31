package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.OtType.OtTypeRequest;
import com.example.demo.dto.humanresource.OtType.OtTypeResponse;
import com.example.demo.entity.humanresource.OtType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OtTypeMapper {
    OtType toOtType(OtTypeRequest request);

    OtTypeResponse toOtTypeResponse(OtType otType);

    void updateOtType(@MappingTarget OtType otType, OtTypeRequest request);
}
