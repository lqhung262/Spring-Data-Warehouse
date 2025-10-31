package com.example.demo.dto.humanresource.MedicalFacility;

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
public class MedicalFacilityRequest {
    @NotBlank
    private String medicalFacilityCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;

    @NotNull
    private Long provinceCityId;
}
