package com.example.demo.dto.humanresource.LaborStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LaborStatusRequest {
    @NotBlank
    private String laborStatusCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
