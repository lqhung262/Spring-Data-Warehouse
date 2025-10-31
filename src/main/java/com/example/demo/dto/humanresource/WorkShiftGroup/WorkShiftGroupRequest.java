package com.example.demo.dto.humanresource.WorkShiftGroup;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkShiftGroupRequest {
    @NotBlank
    private String workShiftGroupCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
