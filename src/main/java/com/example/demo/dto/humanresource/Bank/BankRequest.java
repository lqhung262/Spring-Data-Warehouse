package com.example.demo.dto.humanresource.Bank;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankRequest {
    @NotBlank
    private String bankCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;

    @NotBlank
    private String shortName;
}
