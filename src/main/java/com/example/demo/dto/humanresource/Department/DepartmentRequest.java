package com.example.demo.dto.humanresource.Department;

import com.example.demo.dto.common.BaseRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Request DTO for Department operations.
 * Extends BaseRequest to inherit sourceId and sourceSystemId fields.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DepartmentRequest extends BaseRequest {
    
    @NotBlank(message = "Department code is required")
    private String departmentCode;

    @NotBlank(message = "Name is required")
    private String name;
}
