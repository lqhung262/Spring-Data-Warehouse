package com.example.demo.dto.humanresource.EducationLevel;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationLevelRequest {
    @NotBlank
    private String educationLevelCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
