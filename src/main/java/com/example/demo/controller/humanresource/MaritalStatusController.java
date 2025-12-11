package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusRequest;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.MaritalStatusService;
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
@RequestMapping("/api/v1/human-resource/marital-statuses")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MaritalStatusController {
    MaritalStatusService maritalStatusService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<MaritalStatusResponse> createMaritalStatus(@Valid @RequestBody MaritalStatusRequest request) {
        ApiResponse<MaritalStatusResponse> response = new ApiResponse<>();

        response.setResult(maritalStatusService.createMaritalStatus(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertMaritalStatuses(
            @RequestBody List<MaritalStatusRequest> requests) {
        log.info("Received bulk upsert request for {} marital statuses", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("MARITAL_STATUS", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.MARITAL_STATUS_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "MARITAL_STATUS", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteMaritalStatuses(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} marital statuses", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("MARITAL_STATUS", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.MARITAL_STATUS_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "MARITAL_STATUS", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<MaritalStatusResponse>> getMaritalStatuss(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                               @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                               @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                               @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<MaritalStatusResponse>>builder()
                .result(maritalStatusService.getMaritalStatuses(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{maritalStatusId}")
    ApiResponse<MaritalStatusResponse> getMaritalStatus(@PathVariable("maritalStatusId") Long maritalStatusId) {
        return ApiResponse.<MaritalStatusResponse>builder()
                .result(maritalStatusService.getMaritalStatus(maritalStatusId))
                .build();
    }

    @PutMapping("/{maritalStatusId}")
    ApiResponse<MaritalStatusResponse> updateMaritalStatus(@PathVariable("maritalStatusId") Long maritalStatusId, @RequestBody MaritalStatusRequest request) {
        return ApiResponse.<MaritalStatusResponse>builder()
                .result(maritalStatusService.updateMaritalStatus(maritalStatusId, request))
                .build();
    }

    @DeleteMapping("/{maritalStatusId}")
    ApiResponse<String> deleteMaritalStatus(@PathVariable Long maritalStatusId) {
        maritalStatusService.deleteMaritalStatus(maritalStatusId);
        return ApiResponse.<String>builder().result("Marital Status has been deleted").build();
    }
}
