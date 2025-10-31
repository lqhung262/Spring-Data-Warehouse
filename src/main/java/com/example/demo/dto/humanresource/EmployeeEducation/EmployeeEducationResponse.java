package com.example.demo.dto.humanresource.EmployeeEducation;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmployeeEducationResponse {
    Long employeeEducationId;
    Long employeeId;
    Long majorId;
    Long specializationId;
    Long educationLevelId;
    Long schoolId;
}
