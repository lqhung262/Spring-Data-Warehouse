package com.example.demo.dto.humanresource.WorkShift;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkShiftRequest {
    @NotBlank
    private String workShiftCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
