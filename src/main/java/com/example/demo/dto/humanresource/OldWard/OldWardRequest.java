package com.example.demo.dto.humanresource.OldWard;

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
public class OldWardRequest {
    @NotNull
    private Long wardId;

    @NotBlank
    private String sourceId;

    @NotNull
    private Long oldDistrictId;

    @NotBlank
    private String name;
}
