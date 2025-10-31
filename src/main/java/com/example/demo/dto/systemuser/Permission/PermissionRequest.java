package com.example.demo.dto.systemuser.Permission;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionRequest {
    @NotBlank
    private String shortName;

    @NotBlank
    private String description;

    @NotBlank
    private String url;

    @NotBlank
    private String method;

}
