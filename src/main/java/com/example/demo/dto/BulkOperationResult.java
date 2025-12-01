package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic DTO for bulk operation results with detailed success/failure information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkOperationResult<T> {
    private int totalRequests;
    private int successCount;
    private int failedCount;
    private List<T> successResults;
    private List<BulkOperationError> errors;
    private String summary;

    public boolean hasErrors() {
        return failedCount > 0;
    }

    public boolean hasSuccess() {
        return successCount > 0;
    }
}