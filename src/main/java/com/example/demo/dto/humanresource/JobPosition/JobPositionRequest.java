package com.example.demo.dto.humanresource.JobPosition;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPositionRequest {
    @NotBlank
    private String jobPositionCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
