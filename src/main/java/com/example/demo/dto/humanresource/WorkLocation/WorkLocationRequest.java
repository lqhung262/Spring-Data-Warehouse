package com.example.demo.dto.humanresource.WorkLocation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkLocationRequest {
    @NotBlank
    private String workLocationCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;


}
