package com.example.demo.dto.humanresource.Nationality;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NationalityRequest {
    @NotBlank
    private String nationalityCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
