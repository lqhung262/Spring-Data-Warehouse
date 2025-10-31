package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeRequest;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeResponse;
import com.example.demo.entity.humanresource.AttendanceType;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AttendanceTypeMapper {
    AttendanceType toAttendanceType(AttendanceTypeRequest request);

    AttendanceTypeResponse toAttendanceTypeResponse(AttendanceType type);

    void updateAttendanceType(@MappingTarget AttendanceType type, AttendanceTypeRequest request);
}