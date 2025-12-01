package com.example.demo.util.bulk;

import com.example.demo.dto.BulkOperationError;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Processor cho Final Batch (save + flush individual)
 * Reusable cho tất cả entities
 */
@Slf4j
public class FinalBatchProcessor<TRequest, TEntity, TResponse> {

    private final BulkUpsertConfig<TRequest, TEntity, TResponse> config;

    public FinalBatchProcessor(BulkUpsertConfig<TRequest, TEntity, TResponse> config) {
        this.config = config;
    }

    /**
     * Process final batch với individual save + flush
     */
    public void process(
            List<TRequest> finalBatch,
            List<TResponse> successResults,
            List<BulkOperationError> errors,
            int startIndex) {

        log.debug("Processing final batch: {} requests", finalBatch.size());

        // duyệt từng request trong final batch, startIndex = index bắt đầu của final batch trong tổng requests
        for (int i = 0; i < finalBatch.size(); i++) {
            TRequest request = finalBatch.get(i);
            int globalIndex = startIndex + i;

            try {
                // Tìm entity tồn tại
                TEntity existingEntity = config.getExistingEntityFinder().apply(request);
                TEntity entity;

                if (existingEntity != null) { // tồn tại
                    // UPDATE
                    entity = existingEntity;
                    config.getEntityUpdater().update(entity, request);
                    log.trace("Final batch [{}]: Updating entity", globalIndex);
                } else {
                    // CREATE
                    entity = config.getRequestToEntityMapper().apply(request);
                    log.trace("Final batch [{}]: Creating entity", globalIndex);
                }

                // Call save() method from RepositorySave interface
                TEntity saved = config.getRepositorySaveAndFlusher().save(entity);

                // Clear persistence context, tránh việc entity cũ giữ reference gây stale state
                if (config.getEntityManagerClearer() != null) {
                    config.getEntityManagerClearer().run();
                }

                TResponse response = config.getEntityToResponseMapper().apply(saved);
                successResults.add(response);

            } catch (Exception e) {
                log.error("Final batch [{}]: Failed - {}", globalIndex, e.getMessage());
                errors.add(buildError(globalIndex, request, e));
            }
        }
    }

    /**
     * Build error object
     */
    private BulkOperationError buildError(int index, TRequest request, Exception e) {
        String identifier = String.valueOf(request);
        String errorMessage = "Save failed" + ": " +
                (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());

        return BulkOperationError.builder()
                .index(index)
                .identifier(identifier)
                .errorMessage(errorMessage)
                .errorType(e.getClass().getSimpleName())
                .build();
    }
}