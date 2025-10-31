package com.example.demo.dto.humanresource.MaritalStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaritalStatusRequest {
    @NotBlank
    private String maritalStatusCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
