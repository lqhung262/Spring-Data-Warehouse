package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.WorkShift.WorkShiftRequest;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftResponse;
import com.example.demo.entity.humanresource.WorkShift;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkShiftMapper {
    WorkShift toWorkShift(WorkShiftRequest request);

    WorkShiftResponse toWorkShiftResponse(WorkShift workShift);

    void updateWorkShift(@MappingTarget WorkShift workShift, WorkShiftRequest request);
}