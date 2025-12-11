package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.JobRank.JobRankRequest;
import com.example.demo.dto.humanresource.JobRank.JobRankResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.JobRankService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/human-resource/job-ranks")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobRankController {
    JobRankService jobRankService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<JobRankResponse> createJobRank(@Valid @RequestBody JobRankRequest request) {
        ApiResponse<JobRankResponse> response = new ApiResponse<>();

        response.setResult(jobRankService.createJobRank(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertJobRanks(
            @RequestBody List<JobRankRequest> requests) {
        log.info("Received bulk upsert request for {} job ranks", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("JOB_RANK", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.JOB_RANK_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "JOB_RANK", OperationType.UPSERT, requests.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk upsert request accepted")
                .result(response)
                .build();
    }

    /**
     * BULK DELETE ENDPOINT
     */
    @DeleteMapping("/bulk-delete")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkDeleteJobRanks(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} job ranks", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("JOB_RANK", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.JOB_RANK_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "JOB_RANK", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<JobRankResponse>> getJobRanks(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                   @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                   @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                   @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<JobRankResponse>>builder()
                .result(jobRankService.getJobRanks(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{jobRankId}")
    ApiResponse<JobRankResponse> getJobRank(@PathVariable("jobRankId") Long jobRankId) {
        return ApiResponse.<JobRankResponse>builder()
                .result(jobRankService.getJobRank(jobRankId))
                .build();
    }

    @PutMapping("/{jobRankId}")
    ApiResponse<JobRankResponse> updateJobRank(@PathVariable("jobRankId") Long jobRankId, @RequestBody JobRankRequest request) {
        return ApiResponse.<JobRankResponse>builder()
                .result(jobRankService.updateJobRank(jobRankId, request))
                .build();
    }

    @DeleteMapping("/{jobRankId}")
    ApiResponse<String> deleteJobRank(@PathVariable Long jobRankId) {
        jobRankService.deleteJobRank(jobRankId);
        return ApiResponse.<String>builder().result("Job Rank has been deleted").build();
    }
}
