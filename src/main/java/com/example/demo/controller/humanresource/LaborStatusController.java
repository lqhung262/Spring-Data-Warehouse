package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.LaborStatus.LaborStatusRequest;
import com.example.demo.dto.humanresource.LaborStatus.LaborStatusResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.LaborStatusService;
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
@RequestMapping("/api/v1/human-resource/labor-statuses")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LaborStatusController {
    LaborStatusService laborStatusService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<LaborStatusResponse> createLaborStatus(@Valid @RequestBody LaborStatusRequest request) {
        ApiResponse<LaborStatusResponse> response = new ApiResponse<>();

        response.setResult(laborStatusService.createLaborStatus(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertLaborStatuses(
            @RequestBody List<LaborStatusRequest> requests) {
        log.info("Received bulk upsert request for {} labor statuses", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("LABOR_STATUS", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.LABOR_STATUS_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "LABOR_STATUS", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteLaborStatuses(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} labor statuses", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("LABOR_STATUS", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.LABOR_STATUS_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "LABOR_STATUS", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<LaborStatusResponse>> getLaborStatuses(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                            @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                            @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<LaborStatusResponse>>builder()
                .result(laborStatusService.getLaborStatuses(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{laborStatusId}")
    ApiResponse<LaborStatusResponse> getLaborStatus(@PathVariable("laborStatusId") Long laborStatusId) {
        return ApiResponse.<LaborStatusResponse>builder()
                .result(laborStatusService.getLaborStatus(laborStatusId))
                .build();
    }

    @PutMapping("/{laborStatusId}")
    ApiResponse<LaborStatusResponse> updateLaborStatus(@PathVariable("laborStatusId") Long laborStatusId, @RequestBody LaborStatusRequest request) {
        return ApiResponse.<LaborStatusResponse>builder()
                .result(laborStatusService.updateLaborStatus(laborStatusId, request))
                .build();
    }

    @DeleteMapping("/{laborStatusId}")
    ApiResponse<String> deleteLaborStatus(@PathVariable Long laborStatusId) {
        laborStatusService.deleteLaborStatus(laborStatusId);
        return ApiResponse.<String>builder().result("Labor Status has been deleted").build();
    }
}
