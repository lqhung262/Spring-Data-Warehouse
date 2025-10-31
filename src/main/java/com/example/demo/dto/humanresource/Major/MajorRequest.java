package com.example.demo.dto.humanresource.Major;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorRequest {
    @NotBlank
    private String majorCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
