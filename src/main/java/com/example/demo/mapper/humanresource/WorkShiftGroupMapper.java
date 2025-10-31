package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupRequest;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupResponse;
import com.example.demo.entity.humanresource.WorkShiftGroup;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkShiftGroupMapper {
    WorkShiftGroup toWorkShiftGroup(WorkShiftGroupRequest request);

    WorkShiftGroupResponse toWorkShiftGroupResponse(WorkShiftGroup group);

    void updateWorkShiftGroup(@MappingTarget WorkShiftGroup group, WorkShiftGroupRequest request);
}