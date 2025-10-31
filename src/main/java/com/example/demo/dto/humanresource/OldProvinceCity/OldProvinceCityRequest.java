package com.example.demo.dto.humanresource.OldProvinceCity;

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
public class OldProvinceCityRequest {
    @NotNull
    private Long provinceCityId;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
