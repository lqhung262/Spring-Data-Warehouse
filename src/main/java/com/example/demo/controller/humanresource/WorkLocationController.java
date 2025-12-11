package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.WorkLocation.WorkLocationRequest;
import com.example.demo.dto.humanresource.WorkLocation.WorkLocationResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.WorkLocationService;
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
@RequestMapping("/api/v1/human-resource/work-locations")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkLocationController {
    WorkLocationService workLocationService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<WorkLocationResponse> createWorkLocation(@Valid @RequestBody WorkLocationRequest request) {
        ApiResponse<WorkLocationResponse> response = new ApiResponse<>();

        response.setResult(workLocationService.createWorkLocation(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertWorkLocations(
            @RequestBody List<WorkLocationRequest> requests) {
        log.info("Received bulk upsert request for {} work locations", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("WORK_LOCATION", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.WORK_LOCATION_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "WORK_LOCATION", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteWorkLocations(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} work locations", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("WORK_LOCATION", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.WORK_LOCATION_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "WORK_LOCATION", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<WorkLocationResponse>> getWorkLocations(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                             @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<WorkLocationResponse>>builder()
                .result(workLocationService.getWorkLocations(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{workLocationId}")
    ApiResponse<WorkLocationResponse> getWorkLocation(@PathVariable("workLocationId") Long workLocationId) {
        return ApiResponse.<WorkLocationResponse>builder()
                .result(workLocationService.getWorkLocation(workLocationId))
                .build();
    }

    @PutMapping("/{workLocationId}")
    ApiResponse<WorkLocationResponse> updateWorkLocation(@PathVariable("workLocationId") Long workLocationId, @RequestBody WorkLocationRequest request) {
        return ApiResponse.<WorkLocationResponse>builder()
                .result(workLocationService.updateWorkLocation(workLocationId, request))
                .build();
    }

    @DeleteMapping("/{workLocationId}")
    ApiResponse<String> deleteWorkLocation(@PathVariable Long workLocationId) {
        workLocationService.deleteWorkLocation(workLocationId);
        return ApiResponse.<String>builder().result("Work Location has been deleted").build();
    }
}
