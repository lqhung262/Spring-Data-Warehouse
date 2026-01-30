package com.example.demo.dto.humanresource.EmployeeWorkShift;

import com.example.demo.dto.common.RefDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Response DTO for EmployeeWorkShift.
 * Uses RefDto for related entities to provide clean, structured data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeWorkShiftResponse {
    
    Long id;
    String attendanceCode;
    Boolean saturdayFull;
    
    // Related entities as RefDto
    RefDto workShift;
    RefDto workShiftGroup;
    RefDto attendanceType;
    RefDto otType;
}
