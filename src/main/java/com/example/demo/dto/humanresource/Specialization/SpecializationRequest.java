package com.example.demo.dto.humanresource.Specialization;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpecializationRequest {
    @NotBlank
    private String specializationCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
