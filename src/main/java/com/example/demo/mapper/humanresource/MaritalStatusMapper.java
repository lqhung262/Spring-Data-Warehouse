package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusRequest;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusResponse;
import com.example.demo.entity.humanresource.MaritalStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MaritalStatusMapper {
    MaritalStatus toMaritalStatus(MaritalStatusRequest request);

    MaritalStatusResponse toMaritalStatusResponse(MaritalStatus maritalStatus);

    void updateMaritalStatus(@MappingTarget MaritalStatus maritalStatus, MaritalStatusRequest request);
}