package com.example.demo.util.bulk;

import com.example.demo.dto.BulkOperationError;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Processor cho Safe Batch (saveAll)
 * Reusable cho tất cả entities
 */
@Slf4j
public class SafeBatchProcessor<TRequest, TEntity, TResponse> {

    private final BulkUpsertConfig<TRequest, TEntity, TResponse> config;

    public SafeBatchProcessor(BulkUpsertConfig<TRequest, TEntity, TResponse> config) {
        this.config = config;
    }

    /**
     * Process safe batch với saveAll
     */
    public void process(
            List<TRequest> safeBatch,
            List<TResponse> successResults,
            List<BulkOperationError> errors,
            int startIndex) {

        log.debug("Processing safe batch: {} requests", safeBatch.size());

        // Build existing entity map for UPDATE detection
        Map<TEntity, TRequest> entityToRequestMap = new HashMap<>();
        List<TEntity> entitiesToSave = new ArrayList<>();

        // duyệt từng request trong safe batch
        for (int i = 0; i < safeBatch.size(); i++) {
            TRequest request = safeBatch.get(i);
            int globalIndex = startIndex + i;

            try {
                // Check if entity exists
                TEntity existingEntity = config.getExistingEntityFinder().apply(request);
                TEntity entity;

                if (existingEntity != null) { // Exists
                    // UPDATE
                    entity = existingEntity;
                    config.getEntityUpdater().update(entity, request);
                    log.trace("Safe batch [{}]: Updating entity", globalIndex);
                } else {
                    // CREATE
                    entity = config.getRequestToEntityMapper().apply(request);
                    log.trace("Safe batch [{}]: Creating entity", globalIndex);
                }

                entitiesToSave.add(entity);
                entityToRequestMap.put(entity, request);

            } catch (Exception e) {
                log.error("Safe batch [{}]: Preparation failed - {}", globalIndex, e.getMessage());
                errors.add(buildError(globalIndex, request, "Preparation failed", e));
            }
        }

        // Batch save
        if (!entitiesToSave.isEmpty()) {
            try {
                // Call saveAll() method from RepositorySaveAll interface
                Iterable<TEntity> savedIterable = config.getRepositorySaver().saveAll(entitiesToSave);

                // Convert to responses
                for (TEntity saved : savedIterable) {
                    TResponse response = config.getEntityToResponseMapper().apply(saved);
                    successResults.add(response);
                }

            } catch (Exception e) {
                log.error("Safe batch: saveAll failed - {}", e.getMessage());

                // Mark all as errors if batch save fails
                for (int i = 0; i < entitiesToSave.size(); i++) {
                    int globalIndex = startIndex + i;
                    TEntity entity = entitiesToSave.get(i);
                    TRequest request = entityToRequestMap.get(entity);
                    errors.add(buildError(globalIndex, request, "Batch save failed", e));
                }
            }
        }
    }

    /**
     * Build error object
     */
    private BulkOperationError buildError(int index, TRequest request, String context, Exception e) {
        String identifier = String.valueOf(request); // Override toString in request DTO
        String errorMessage = context + ": " +
                (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());

        return BulkOperationError.builder()
                .index(index)
                .identifier(identifier)
                .errorMessage(errorMessage)
                .errorType(e.getClass().getSimpleName())
                .build();
    }
}