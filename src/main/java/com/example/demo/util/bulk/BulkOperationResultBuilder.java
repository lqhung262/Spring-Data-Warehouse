package com.example.demo.util.bulk;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.BulkOperationError;

import java.util.List;

/**
 * Builder cho BulkOperationResult
 * Reusable cho tất cả entities
 */
public class BulkOperationResultBuilder {

    private BulkOperationResultBuilder() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Build result từ success/error lists
     */
    public static <T> BulkOperationResult<T> build(
            int totalRequests,
            List<T> successResults,
            List<BulkOperationError> errors,
            long durationMs) {

        int successCount = successResults.size();
        int failedCount = errors.size();

        String summary = String.format(
                "Bulk operation completed: %d/%d succeeded, %d/%d failed (%.2fs)",
                successCount, totalRequests,
                failedCount, totalRequests,
                durationMs / 1000.0
        );

        return BulkOperationResult.<T>builder()
                .totalRequests(totalRequests)
                .successCount(successCount)
                .failedCount(failedCount)
                .successResults(successResults)
                .errors(errors)
                .summary(summary)
                .build();
    }
}