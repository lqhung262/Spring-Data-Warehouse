package com.example.demo.kafka.service;


import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.kafka.JobStatusResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.entity.kafka.KafkaJobStatus;
import com.example.demo.exception.NotFoundException;
import com.example.demo.kafka.enums.JobStatus;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.repository.KafkaJobStatusRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaJobStatusService {

    private final KafkaJobStatusRepository jobStatusRepository;
    private final ObjectMapper objectMapper;

    /**
     * Tạo job status khi submit request lên Kafka
     */
    @Transactional
    public String createJob(String entityType, OperationType operationType, int totalRecords) {
        String jobId = UUID.randomUUID().toString();

        KafkaJobStatus jobStatus = KafkaJobStatus.builder()
                .jobId(jobId)
                .entityType(entityType)
                .operationType(operationType)
                .totalRecords(totalRecords)
                .processedRecords(0)
                .successCount(0)
                .failureCount(0)
                .status(JobStatus.QUEUED)
                .build();

        jobStatusRepository.save(jobStatus);
        log.info("Created job status: jobId={}, entityType={}, operationType={}, totalRecords={}",
                jobId, entityType, operationType, totalRecords);

        return jobId;
    }

    /**
     * Cập nhật status thành PROCESSING khi consumer bắt đầu xử lý
     */
    @Transactional
    public void updateToProcessing(String jobId) {
        KafkaJobStatus jobStatus = findByJobId(jobId);
        jobStatus.setStatus(JobStatus.PROCESSING);
        jobStatusRepository.save(jobStatus);
        log.info("Updated job to PROCESSING: jobId={}", jobId);
    }

    /**
     * Cập nhật kết quả sau khi consumer xử lý xong
     */
    @Transactional
    public void updateJobResult(String jobId, BulkOperationResult<?> result) {
        KafkaJobStatus jobStatus = findByJobId(jobId);

        jobStatus.setProcessedRecords(result.getTotalRequests());
        jobStatus.setSuccessCount(result.getSuccessCount());
        jobStatus.setFailureCount(result.getFailedCount());
        jobStatus.setStatus(JobStatus.COMPLETED);
        jobStatus.setCompletedAt(LocalDateTime.now());

        // Serialize result to JSON
        try {
            String resultJson = objectMapper.writeValueAsString(result);
            jobStatus.setResultJson(resultJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize result for jobId:  {}", jobId, e);
        }

        jobStatusRepository.save(jobStatus);
        log.info("Updated job result: jobId={}, success={}, failure={}",
                jobId, result.getSuccessCount(), result.getFailedCount());
    }

    /**
     * Đánh dấu job thất bại
     */
    @Transactional
    public void markAsFailed(String jobId, String errorMessage) {
        KafkaJobStatus jobStatus = findByJobId(jobId);
        jobStatus.setStatus(JobStatus.FAILED);
        jobStatus.setResultJson(errorMessage);
        jobStatus.setCompletedAt(LocalDateTime.now());
        jobStatusRepository.save(jobStatus);
        log.error("Marked job as FAILED: jobId={}, error={}", jobId, errorMessage);
    }

    /**
     * Lấy job status theo jobId
     */
    public JobStatusResponse getJobStatus(String jobId) {
        KafkaJobStatus jobStatus = findByJobId(jobId);

        BulkOperationResult<?> result = null;
        if (jobStatus.getResultJson() != null && jobStatus.getStatus() == JobStatus.COMPLETED) {
            try {
                result = objectMapper.readValue(jobStatus.getResultJson(), BulkOperationResult.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize result for jobId: {}", jobId, e);
            }
        }

        return JobStatusResponse.builder()
                .jobId(jobStatus.getJobId())
                .entityType(jobStatus.getEntityType())
                .operationType(jobStatus.getOperationType().name())
                .status(jobStatus.getStatus().name())
                .totalRecords(jobStatus.getTotalRecords())
                .processedRecords(jobStatus.getProcessedRecords())
                .successCount(jobStatus.getSuccessCount())
                .failureCount(jobStatus.getFailureCount())
                .result(result)
                .createdAt(jobStatus.getCreatedAt())
                .updatedAt(jobStatus.getUpdatedAt())
                .completedAt(jobStatus.getCompletedAt())
                .build();
    }

    /**
     * Tạo JobSubmissionResponse
     */
    public JobSubmissionResponse createSubmissionResponse(String jobId, String entityType,
                                                          OperationType operationType, int totalRecords) {
        return JobSubmissionResponse.builder()
                .jobId(jobId)
                .entityType(entityType)
                .operationType(operationType.name())
                .totalRecords(totalRecords)
                .status(JobStatus.QUEUED.name())
                .message("Request accepted and queued for processing")
                .statusUrl("/api/v1/kafka-jobs/" + jobId + "/status")
                .build();
    }

    private KafkaJobStatus findByJobId(String jobId) {
        return jobStatusRepository.findByJobId(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found with id: " + jobId));
    }
}