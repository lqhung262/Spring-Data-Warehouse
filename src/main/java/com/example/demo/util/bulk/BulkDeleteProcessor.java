package com.example.demo.util.bulk;

import com.example.demo.dto.BulkOperationError;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.exception.CannotDeleteException;
import com.example.demo.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Processor for bulk delete operations with partial success
 * Reusable cho tất cả entities
 */
@Slf4j
public class BulkDeleteProcessor<TEntity> {

    private final BulkDeleteConfig<TEntity> config;

    public BulkDeleteProcessor(BulkDeleteConfig<TEntity> config) {
        this.config = config;
    }

    /**
     * Execute bulk delete với partial success pattern
     * Returns detailed result with success/failure breakdown
     */
    public BulkOperationResult<Long> execute(List<Long> ids) {
        log.info("Starting bulk delete for {} items", ids.size());
        long startTime = System.currentTimeMillis();

        List<BulkOperationError> errors = new ArrayList<>();

        // 1. Validate và extract unique IDs với index mapping (single pass)
        ValidationResult validationResult = validateAndExtractUniqueIds(ids, errors);

        // 2. Process deletion for each ID with O(1) index lookup
        List<Long> successResults = processDeleteOperations(validationResult, errors);

        // 3. Build and return result
        return buildDeleteResult(ids.size(), successResults, errors, startTime);
    }

    /**
     * Validate IDs và collect duplicates as errors in single pass
     * Returns unique IDs with their first occurrence index (O(n) complexity)
     */
    private ValidationResult validateAndExtractUniqueIds(List<Long> ids, List<BulkOperationError> errors) {
        Map<Long, Integer> idToFirstIndex = new LinkedHashMap<>(); // Preserves insertion order
        Set<Long> duplicates = new HashSet<>();

        for (int i = 0; i < ids.size(); i++) {
            Long id = ids.get(i);

            if (idToFirstIndex.containsKey(id)) {
                // Duplicate found - add error for this occurrence
                duplicates.add(id);
                errors.add(buildError(i, id, "Duplicate ID in request", null));
            } else {
                // First occurrence - record index
                idToFirstIndex.put(id, i);
            }
        }

        if (!duplicates.isEmpty()) {
            log.warn("Found {} duplicate IDs in request: {}", duplicates.size(), duplicates);
        }

        return new ValidationResult(idToFirstIndex);
    }

    /**
     * Process delete operations for all unique IDs with O(1) index lookup
     */
    private List<Long> processDeleteOperations(ValidationResult validationResult, List<BulkOperationError> errors) {
        List<Long> successResults = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : validationResult.idToIndexMap().entrySet()) {
            Long id = entry.getKey();
            int globalIndex = entry.getValue();

            processDeleteSingleId(id, globalIndex, successResults, errors);
        }

        return successResults;
    }

    /**
     * Process delete for a single ID
     * FK checker validates existence internally, so no separate check needed
     */
    private void processDeleteSingleId(Long id, int globalIndex, List<Long> successResults, List<BulkOperationError> errors) {
        try {
            // FK constraint checker validates existence + checks references (combined)
            checkForeignKeyConstraints(id);

            // Delete entity
            deleteEntity(id);

            successResults.add(id);
            log.debug("Successfully deleted {} with ID: {}", config.getEntityName(), id);

        } catch (NotFoundException e) {
            handleNotFoundException(id, globalIndex, errors, e);
        } catch (CannotDeleteException e) {
            handleCannotDeleteException(id, globalIndex, errors, e);
        } catch (Exception e) {
            handleUnexpectedException(id, globalIndex, errors, e);
        }
    }

    /**
     * Check foreign key constraints (also validates existence)
     */
    private void checkForeignKeyConstraints(Long id) {
        config.getForeignKeyConstraintsChecker().accept(id);
    }

    /**
     * Delete entity by ID
     */
    private void deleteEntity(Long id) {
        config.getRepositoryDeleter().accept(id);
    }

    /**
     * Handle NotFoundException during delete
     */
    private void handleNotFoundException(Long id, int globalIndex, List<BulkOperationError> errors, NotFoundException e) {
        log.error("Delete failed [{}]: {} not found with ID: {}", globalIndex, config.getEntityName(), id);
        errors.add(buildError(globalIndex, id, "Not found", e));
    }

    /**
     * Handle CannotDeleteException during delete
     */
    private void handleCannotDeleteException(Long id, int globalIndex, List<BulkOperationError> errors, CannotDeleteException e) {
        log.error("Delete failed [{}]: FK constraint violation for ID: {}", globalIndex, id);
        errors.add(buildError(globalIndex, id, "Foreign key constraint", e));
    }

    /**
     * Handle unexpected exception during delete
     */
    private void handleUnexpectedException(Long id, int globalIndex, List<BulkOperationError> errors, Exception e) {
        log.error("Delete failed [{}]: Unexpected error for ID: {}", globalIndex, id, e);
        errors.add(buildError(globalIndex, id, "Delete failed", e));
    }

    /**
     * Build final result with summary
     */
    private BulkOperationResult<Long> buildDeleteResult(int totalRequests, List<Long> successResults,
                                                        List<BulkOperationError> errors, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        BulkOperationResult<Long> result = BulkOperationResultBuilder.build(
                totalRequests,
                successResults,
                errors,
                duration
        );

        log.info("Bulk delete completed: {}/{} succeeded, {}/{} failed in {}ms",
                result.getSuccessCount(), result.getTotalRequests(),
                result.getFailedCount(), result.getTotalRequests(),
                duration);

        return result;
    }

    /**
     * Build error object for delete operation
     */
    private BulkOperationError buildError(int index, Long id, String context, Exception e) {
        String identifier = "ID=" + id;
        String errorMessage = context;

        if (e != null && e.getMessage() != null) {
            errorMessage += ": " + e.getMessage();
        }

        return BulkOperationError.builder()
                .index(index)
                .identifier(identifier)
                .errorMessage(errorMessage)
                .errorType(e != null ? e.getClass().getSimpleName() : "ValidationError")
                .build();
    }

    /**
     * Inner class to hold validation results
     * Maps each unique ID to its first occurrence index for O(1) lookup
     */
    private record ValidationResult(Map<Long, Integer> idToIndexMap) {
    }
}