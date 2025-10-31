package com.example.demo.dto.general.Country;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryRequest {
    @NotBlank
    private String countryCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
