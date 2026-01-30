package com.example.demo.dto.common;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base class for simple catalog/lookup table request DTOs.
 * Extends BaseRequest with additional common fields for master data:
 * - code: Unique business code for the entity
 * - name: Display name
 *
 * Use this for entities like Bank, Department, School, etc.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseCatalogRequest extends BaseRequest {
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotBlank(message = "Name is required")
    private String name;
}
