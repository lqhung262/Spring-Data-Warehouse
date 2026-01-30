package com.example.demo.dto.common;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Base class for all request DTOs.
 * Contains common fields that are required for data warehouse operations:
 * - sourceId: Unique identifier from source system
 * - sourceSystemId: Reference to the source system
 *
 * These fields are used internally for data lineage and should not be
 * exposed in response DTOs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseRequest {
    
    @NotBlank(message = "Source ID is required")
    private String sourceId;
    
    @NotNull(message = "Source System ID is required")
    private Long sourceSystemId;
}
