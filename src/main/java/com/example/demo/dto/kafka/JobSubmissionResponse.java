package com.example.demo.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSubmissionResponse {
    private String jobId;
    private String entityType;
    private String operationType;
    private Integer totalRecords;
    private String status;
    private String message;
    private String statusUrl;
}