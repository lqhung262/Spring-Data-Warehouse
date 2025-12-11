package com.example.demo.kafka.processor;


import com.example.demo.dto.BulkOperationResult;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.exception.RetryableException;
import com.example.demo.kafka.model.KafkaMessage;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProcessor {

    private final ObjectMapper objectMapper;
    private final KafkaJobStatusService jobStatusService;
    private final BulkOperationHandlerRegistry handlerRegistry;

    // gán messageSpec + handler function (bulk service functions)
    @PostConstruct
    public void init() {
        handlerRegistry.init();
        log.info("MessageProcessor initialized with {} handlers", MessageSpec.values().length);
    }

    public void processMessage(KafkaMessage<?> kafkaMessage, MessageSpec messageSpec) {
        String jobId = kafkaMessage.getJobId();
        log.info("Processing message with spec: {}, jobId: {}, payload size: {}",
                messageSpec, jobId, kafkaMessage.getPayload().size());

        try {
            // Update status to PROCESSING
            jobStatusService.updateToProcessing(jobId);

            // Get handler from registry
            BulkOperationHandlerConfig<?> handlerConfig = handlerRegistry.getHandler(messageSpec);

            if (handlerConfig == null) {
                log.warn("No handler found for message spec: {}", messageSpec);
                jobStatusService.markAsFailed(jobId, "Unknown message spec: " + messageSpec);
                return;
            }

            // Process based on operation type (UPSERT or DELETE)
            BulkOperationResult<?> result;
            if (handlerRegistry.isUpsertOperation(messageSpec)) {
                result = processUpsertOperation(kafkaMessage, handlerConfig);
            } else if (handlerRegistry.isDeleteOperation(messageSpec)) {
                result = processDeleteOperation(kafkaMessage, handlerConfig);
            } else {
                log.warn("Unknown operation type for message spec: {}", messageSpec);
                jobStatusService.markAsFailed(jobId, "Unknown operation type: " + messageSpec);
                return;
            }

            // Update job result => COMPLETED
            if (result != null) {
                jobStatusService.updateJobResult(jobId, result);
                log.info("Job completed: jobId={}, success={}, failure={}",
                        jobId, result.getSuccessCount(), result.getFailedCount());
            }

        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation - retryable for jobId: {}", jobId, e);
            throw new RetryableException("Database constraint violation", e);
        } catch (Exception e) {
            log.error("Error processing message for jobId: {}", jobId, e);
            jobStatusService.markAsFailed(jobId, e.getMessage());

            if (isRetryable(e)) {
                throw new RetryableException("Transient error occurred", e);
            }
            throw e;
        }
    }

    /**
     * Process upsert operation using handler config
     */
    private <T> BulkOperationResult<?> processUpsertOperation(
            KafkaMessage<?> kafkaMessage,
            BulkOperationHandlerConfig<T> handlerConfig) {

        List<T> requests = convertPayload(kafkaMessage.getPayload(), handlerConfig.requestClass());
        return handlerConfig.upsertFunction().apply(requests);
    }

    /**
     * Process delete operation using handler config
     */
    private BulkOperationResult<?> processDeleteOperation(
            KafkaMessage<?> kafkaMessage,
            BulkOperationHandlerConfig<?> handlerConfig) {

        List<Long> ids = convertPayload(kafkaMessage.getPayload(), Long.class);
        return handlerConfig.deleteFunction().apply(ids);
    }

    // ==================== HELPER METHODS ====================
    private <T> List<T> convertPayload(List<?> payload, Class<T> targetClass) {
        return payload.stream()
                .map(item -> objectMapper.convertValue(item, targetClass))
                .toList();
    }

    private boolean isRetryable(Exception e) {
        return e instanceof DataIntegrityViolationException
                || e.getCause() instanceof java.net.SocketTimeoutException
                || e.getCause() instanceof java.sql.SQLTransientException;
    }
}