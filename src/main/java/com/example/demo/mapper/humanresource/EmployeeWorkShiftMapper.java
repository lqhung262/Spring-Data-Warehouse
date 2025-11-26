package com.example.demo.mapper.humanresource;

import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftResponse;
import com.example.demo.entity.humanresource.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeWorkShiftMapper {

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "employeeWorkShiftId", ignore = true)
    @Mapping(target = "workShift", ignore = true)
    @Mapping(target = "workShiftGroup", ignore = true)
    @Mapping(target = "attendanceType", ignore = true)
    @Mapping(target = "otType", ignore = true)
    EmployeeWorkShift toEmployeeWorkShift(EmployeeWorkShiftRequest request);

    @Mapping(target = "workShiftId", source = "workShift.workShiftId")
    @Mapping(target = "workShiftGroupId", source = "workShiftGroup.workShiftGroupId")
    @Mapping(target = "attendanceTypeId", source = "attendanceType.attendanceTypeId")
    @Mapping(target = "otTypeId", source = "otType.otTypeId")
    EmployeeWorkShiftResponse toEmployeeWorkShiftResponse(EmployeeWorkShift employeeWorkShift);

    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "employeeWorkShiftId", ignore = true)
    @Mapping(target = "workShift", ignore = true)
    @Mapping(target = "workShiftGroup", ignore = true)
    @Mapping(target = "attendanceType", ignore = true)
    @Mapping(target = "otType", ignore = true)
    void updateEmployeeWorkShift(@MappingTarget EmployeeWorkShift employeeWorkShift, EmployeeWorkShiftRequest request);

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
