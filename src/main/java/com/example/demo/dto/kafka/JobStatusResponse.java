package com.example.demo.dto.kafka;

import com.example.demo.dto.BulkOperationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobStatusResponse {
    private String jobId;
    private String entityType;
    private String operationType;
    private String status;
    private Integer totalRecords;
    private Integer processedRecords;
    private Integer successCount;
    private Integer failureCount;
    private BulkOperationResult<?> result;  // Chứa kết quả chi tiết
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
