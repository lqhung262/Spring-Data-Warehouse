package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.BloodGroup.BloodGroupRequest;
import com.example.demo.dto.humanresource.BloodGroup.BloodGroupResponse;
import com.example.demo.entity.humanresource.BloodGroup;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BloodGroupMapper {
    BloodGroup toBloodGroup(BloodGroupRequest request);

    BloodGroupResponse toBloodGroupResponse(BloodGroup bloodGroup);

    void updateBloodGroup(@MappingTarget BloodGroup bloodGroup, BloodGroupRequest request);
}