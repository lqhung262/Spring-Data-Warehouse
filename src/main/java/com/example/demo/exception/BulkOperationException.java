package com.example.demo.exception;

import lombok.Getter;

import java.util.List;

/**
 * Exception thrown when bulk operations fail.
 * Contains detailed information about which items failed and why.
 */
@Getter
public class BulkOperationException extends RuntimeException {
    private final int code = 400;
    private final int totalRequests;
    private final int failedCount;
    private final List<String> failedItems;

    /**
     * Constructor for bulk operation failures with detailed failed items
     *
     * @param operation     The operation name (e.g., "Bulk Upsert", "Bulk Delete")
     * @param totalRequests Total number of requests in the batch
     * @param failedItems   List of failed item descriptions
     */
    public BulkOperationException(String operation, int totalRequests, List<String> failedItems) {
        super(buildMessage(operation, totalRequests, failedItems));
        this.totalRequests = totalRequests;
        this.failedCount = failedItems.size();
        this.failedItems = failedItems;
    }

    /**
     * Simplified constructor with just a message
     *
     * @param message The error message
     */
    public BulkOperationException(String message) {
        super(message);
        this.totalRequests = 0;
        this.failedCount = 0;
        this.failedItems = List.of();
    }

    private static String buildMessage(String operation, int totalRequests, List<String> failedItems) {
        return String.format(
                "%s failed for %d/%d requests. Failed items: %s.  Transaction will be rolled back.",
                operation,
                failedItems.size(),
                totalRequests,
                failedItems
        );
    }
}