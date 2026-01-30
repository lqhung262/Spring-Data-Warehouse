package com.example.demo.mapper.humanresource;

import com.example.demo.dto.common.RefDto;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftResponse;
import com.example.demo.entity.humanresource.*;
import com.example.demo.mapper.common.CommonMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for EmployeeWorkShift entity.
 * Uses CommonMapperConfig for shared settings.
 */
@Mapper(config = CommonMapperConfig.class)
public interface EmployeeWorkShiftMapper {

    EmployeeWorkShift toEmployeeWorkShift(EmployeeWorkShiftRequest request);

    @Mapping(target = "id", source = "employeeWorkShiftId")
    @Mapping(target = "workShift", expression = "java(toWorkShiftRef(ws.getWorkShift()))")
    @Mapping(target = "workShiftGroup", expression = "java(toWorkShiftGroupRef(ws.getWorkShiftGroup()))")
    @Mapping(target = "attendanceType", expression = "java(toAttendanceTypeRef(ws.getAttendanceType()))")
    @Mapping(target = "otType", expression = "java(toOtTypeRef(ws.getOtType()))")
    EmployeeWorkShiftResponse toEmployeeWorkShiftResponse(EmployeeWorkShift ws);

    void updateEmployeeWorkShift(@MappingTarget EmployeeWorkShift employeeWorkShift, EmployeeWorkShiftRequest request);

    // Helper methods to convert entities to RefDto
    default RefDto toWorkShiftRef(WorkShift entity) {
        return entity == null ? null : RefDto.of(entity.getWorkShiftId(), entity.getName());
    }
    
    default RefDto toWorkShiftGroupRef(WorkShiftGroup entity) {
        return entity == null ? null : RefDto.of(entity.getWorkShiftGroupId(), entity.getName());
    }
    
    default RefDto toAttendanceTypeRef(AttendanceType entity) {
        return entity == null ? null : RefDto.of(entity.getAttendanceTypeId(), entity.getName());
    }
    
    default RefDto toOtTypeRef(OtType entity) {
        return entity == null ? null : RefDto.of(entity.getOtTypeId(), entity.getName());
    }

    default void setReferences(EmployeeWorkShift workShift, EmployeeWorkShiftRequest request) {
        if (request.getWorkShiftId() != null) {
            WorkShift ws = new WorkShift();
            ws.setWorkShiftId(request.getWorkShiftId());
            workShift.setWorkShift(ws);
        }
        if (request.getWorkShiftGroupId() != null) {
            WorkShiftGroup wsg = new WorkShiftGroup();
            wsg.setWorkShiftGroupId(request.getWorkShiftGroupId());
            workShift.setWorkShiftGroup(wsg);
        }
        if (request.getAttendanceTypeId() != null) {
            AttendanceType at = new AttendanceType();
            at.setAttendanceTypeId(request.getAttendanceTypeId());
            workShift.setAttendanceType(at);
        }
        if (request.getOtTypeId() != null) {
            OtType ot = new OtType();
            ot.setOtTypeId(request.getOtTypeId());
            workShift.setOtType(ot);
        }
    }
}
