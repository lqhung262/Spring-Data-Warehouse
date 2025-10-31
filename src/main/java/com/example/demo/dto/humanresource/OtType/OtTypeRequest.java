package com.example.demo.dto.humanresource.OtType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtTypeRequest {
    @NotBlank
    private String otTypeCode;

    @NotBlank
    private String name;
}
