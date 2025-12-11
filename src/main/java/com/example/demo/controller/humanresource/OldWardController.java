package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.OldWard.OldWardRequest;
import com.example.demo.dto.humanresource.OldWard.OldWardResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.OldWardService;
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
@RequestMapping("/api/v1/human-resource/old-wards")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OldWardController {
    OldWardService oldWardService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OldWardResponse> createOldWard(@Valid @RequestBody OldWardRequest request) {
        ApiResponse<OldWardResponse> response = new ApiResponse<>();

        response.setResult(oldWardService.createOldWard(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertOldWards(
            @RequestBody List<OldWardRequest> requests) {
        log.info("Received bulk upsert request for {} departments", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("OLD_WARD", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.OLD_WARD_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "OLD_WARD", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteOldWards(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} old wards", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("OLD_WARD", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.OLD_WARD_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "OLD_WARD", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<OldWardResponse>> getOldWards(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                   @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                   @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                   @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<OldWardResponse>>builder()
                .result(oldWardService.getOldWards(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{oldWardId}")
    ApiResponse<OldWardResponse> getOldWard(@PathVariable("oldWardId") Long oldWardId) {
        return ApiResponse.<OldWardResponse>builder()
                .result(oldWardService.getOldWard(oldWardId))
                .build();
    }

    @PutMapping("/{oldWardId}")
    ApiResponse<OldWardResponse> updateOldWard(@PathVariable("oldWardId") Long oldWardId, @RequestBody OldWardRequest request) {
        return ApiResponse.<OldWardResponse>builder()
                .result(oldWardService.updateOldWard(oldWardId, request))
                .build();
    }

    @DeleteMapping("/{oldWardId}")
    ApiResponse<String> deleteOldWard(@PathVariable Long oldWardId) {
        oldWardService.deleteOldWard(oldWardId);
        return ApiResponse.<String>builder().result("Old Ward has been deleted").build();
    }
}
