package com.example.demo.dto.humanresource.BloodGroup;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BloodGroupRequest {
    @NotBlank
    private String bloodGroupCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
