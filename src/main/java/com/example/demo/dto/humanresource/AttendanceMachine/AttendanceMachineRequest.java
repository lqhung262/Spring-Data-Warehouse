package com.example.demo.dto.humanresource.AttendanceMachine;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceMachineRequest {
    @NotBlank
    private String attendanceMachineCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
