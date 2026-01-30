package com.example.demo.dto.humanresource.EmployeeAttendanceMachine;

import com.example.demo.dto.common.RefDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Response DTO for EmployeeAttendanceMachine.
 * Uses RefDto for related entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeAttendanceMachineResponse {
    
    Long id;
    RefDto machine;
}
