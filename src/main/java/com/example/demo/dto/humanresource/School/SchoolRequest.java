package com.example.demo.dto.humanresource.School;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolRequest {
    @NotBlank
    private String schoolCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
