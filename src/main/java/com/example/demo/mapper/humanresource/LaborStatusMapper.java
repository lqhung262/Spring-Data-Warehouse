package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.LaborStatus.LaborStatusRequest;
import com.example.demo.dto.humanresource.LaborStatus.LaborStatusResponse;
import com.example.demo.entity.humanresource.LaborStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LaborStatusMapper {
    LaborStatus toLaborStatus(LaborStatusRequest request);

    LaborStatusResponse toLaborStatusResponse(LaborStatus laborStatus);

    void updateLaborStatus(@MappingTarget LaborStatus laborStatus, LaborStatusRequest request);
}