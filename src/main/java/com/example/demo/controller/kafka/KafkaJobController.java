package com.example.demo.controller.kafka;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.kafka.JobStatusResponse;
import com.example.demo.kafka.service.KafkaJobStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/kafka-jobs")
@RequiredArgsConstructor
@Slf4j
public class KafkaJobController {

    private final KafkaJobStatusService jobStatusService;

    /**
     * Get job status by jobId
     */
    @GetMapping("/{jobId}/status")
    public ApiResponse<JobStatusResponse> getJobStatus(@PathVariable String jobId) {
        log.info("Getting job status for jobId: {}", jobId);

        JobStatusResponse response = jobStatusService.getJobStatus(jobId);

        return ApiResponse.<JobStatusResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Job status retrieved successfully")
                .result(response)
                .build();
    }
}