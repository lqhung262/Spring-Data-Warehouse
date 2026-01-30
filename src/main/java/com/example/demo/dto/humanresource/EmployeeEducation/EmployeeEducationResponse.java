package com.example.demo.dto.humanresource.EmployeeEducation;

import com.example.demo.dto.common.RefDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Response DTO for EmployeeEducation.
 * Uses RefDto for related entities to provide clean, structured data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeEducationResponse {
    
    Long id;
    RefDto major;
    RefDto specialization;
    RefDto educationLevel;
    RefDto school;
}
