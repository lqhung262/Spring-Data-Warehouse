package com.example.demo.util.bulk;

import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.BulkOperationError;
import com.example.demo.util.BulkOperationUtils;
import com.example.demo.util.BulkOperationUtils.BatchClassification;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Main processor for bulk upsert operations
 * Orchestrates safe batch and final batch processing
 * REUSABLE cho tất cả entities
 */
@Slf4j
public class BulkUpsertProcessor<TRequest, TEntity, TResponse> {

    private final BulkUpsertConfig<TRequest, TEntity, TResponse> config;
    private final SafeBatchProcessor<TRequest, TEntity, TResponse> safeBatchProcessor;
    private final FinalBatchProcessor<TRequest, TEntity, TResponse> finalBatchProcessor;

    public BulkUpsertProcessor(BulkUpsertConfig<TRequest, TEntity, TResponse> config) {
        this.config = config;
        this.safeBatchProcessor = new SafeBatchProcessor<>(config);
        this.finalBatchProcessor = new FinalBatchProcessor<>(config);
    }

    /**
     * Execute bulk upsert với full workflow
     */
    public BulkOperationResult<TResponse> execute(List<TRequest> requests) {
        log.info("Starting bulk upsert for {} requests", requests.size());
        long startTime = System.currentTimeMillis();

        // 1. Classify batch
        BatchClassification<TRequest> classification = BulkOperationUtils.classifyBatchByUniqueFields(
                requests,
                config.getUniqueFieldExtractors(),
                config.getExistingValuesMaps()
        );

        // 2. Initialize result tracking
        List<TResponse> successResults = new ArrayList<>();
        List<BulkOperationError> errors = new ArrayList<>();

        // 3. Process safe batch
        if (classification.hasSafeBatch()) {
            log.info("Processing safe batch: {} requests", classification.getSafeBatch().size());
            safeBatchProcessor.process(
                    classification.getSafeBatch(),
                    successResults,
                    errors,
                    0
            );
        }

        // 4.  Process final batch
        if (classification.hasFinalBatch()) {
            log.warn("Processing final batch: {} requests", classification.getFinalBatch().size());
            int finalBatchStartIndex = classification.getSafeBatch().size();
            finalBatchProcessor.process(
                    classification.getFinalBatch(),
                    successResults,
                    errors,
                    finalBatchStartIndex
            );
        }

        // 5. Build result
        long duration = System.currentTimeMillis() - startTime;
        BulkOperationResult<TResponse> result = BulkOperationResultBuilder.build(
                requests.size(),
                successResults,
                errors,
                duration
        );

        log.info("Bulk upsert completed: {}/{} succeeded, {}/{} failed in {}ms",
                result.getSuccessCount(), result.getTotalRequests(),
                result.getFailedCount(), result.getTotalRequests(),
                duration);

        return result;
    }
}