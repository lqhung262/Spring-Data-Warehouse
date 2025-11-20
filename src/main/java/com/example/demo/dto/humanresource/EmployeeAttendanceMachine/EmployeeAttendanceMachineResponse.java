package com.example.demo.dto.humanresource.EmployeeAttendanceMachine;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeAttendanceMachineResponse {
    Long employeeAttendanceMachineId;
    Long machineId;
}
