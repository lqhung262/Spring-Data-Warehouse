package com.example.demo.dto.humanresource.EmployeeWorkShift;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeWorkShiftResponse {
    Long employeeWorkShiftId;
    Long employeeId;
    String attendanceCode;
    Long workShiftId;
    Long workShiftGroupId;
    Long attendanceTypeId;
    Boolean saturdayFull = Boolean.FALSE;
    Long otTypeId;
}
