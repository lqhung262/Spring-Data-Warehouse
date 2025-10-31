package com.example.demo.dto.general.SourceSystem;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceSystemRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
}
