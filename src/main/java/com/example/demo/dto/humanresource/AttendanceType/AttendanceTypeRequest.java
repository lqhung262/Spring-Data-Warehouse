package com.example.demo.dto.humanresource.AttendanceType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceTypeRequest {
    @NotBlank
    private String attendanceTypeCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
