package com.example.demo.dto.humanresource.EmployeeWorkShift;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeWorkShiftRequest {
    @NotNull
    private Long employeeId;

    @NotBlank
    private String attendanceCode;

    @NotNull
    private Long workShiftId;

    @NotNull
    private Long workShiftGroupId;

    @NotNull
    private Long attendanceTypeId;

    @NotNull
    private Boolean saturdayFull = Boolean.FALSE;

    @NotNull
    private Long otTypeId;
}
