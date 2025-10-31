package com.example.demo.dto.humanresource.Ward;

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
public class WardRequest {
    @NotBlank
    private String sourceId;

    @NotNull
    private Long provinceCityId;

    @NotBlank
    private String name;
}
