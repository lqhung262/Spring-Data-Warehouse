package com.example.demo.dto.humanresource.EmployeeEducation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeEducationRequest {
    @NotNull
    private Long employeeId;

    @NotNull
    private Long majorId;

    @NotNull
    private Long specializationId;

    @NotNull
    private Long educationLevelId;

    @NotNull
    private Long schoolId;
}
