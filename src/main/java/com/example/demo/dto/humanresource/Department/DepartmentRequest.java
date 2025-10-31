package com.example.demo.dto.humanresource.Department;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequest {
    @NotBlank
    private String departmentCode;

    @NotBlank
    private String sourceId;

    @NotBlank
    private String name;
}
