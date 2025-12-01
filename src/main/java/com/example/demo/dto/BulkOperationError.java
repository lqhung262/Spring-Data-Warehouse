package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Nested class cho error details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BulkOperationError {
    private int index;

    /**
     * Identifier của request (ví dụ: source_id, name)
     */
    private String identifier;

    /**
     * Chi tiết request bị lỗi (optional)
     */
    private String requestDetails;

    private String errorMessage;

    /**
     * Error type/code (optional)
     */
    private String errorType;
}