package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Specialization.SpecializationRequest;
import com.example.demo.dto.humanresource.Specialization.SpecializationResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.SpecializationService;
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
@RequestMapping("/api/v1/human-resource/specializations")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpecializationController {
    SpecializationService specializationService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<SpecializationResponse> createSpecialization(@Valid @RequestBody SpecializationRequest request) {
        ApiResponse<SpecializationResponse> response = new ApiResponse<>();

        response.setResult(specializationService.createSpecialization(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertSpecializations(
            @RequestBody List<SpecializationRequest> requests) {
        log.info("Received bulk upsert request for {} specializations", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("SPECIALIZATION", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.SPECIALIZATION_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "SPECIALIZATION", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteSpecializations(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} specializations", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("SPECIALIZATION", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.SPECIALIZATION_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "SPECIALIZATION", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<SpecializationResponse>> getSpecializations(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<SpecializationResponse>>builder()
                .result(specializationService.getSpecializations(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{specializationId}")
    ApiResponse<SpecializationResponse> getSpecialization(@PathVariable("specializationId") Long specializationId) {
        return ApiResponse.<SpecializationResponse>builder()
                .result(specializationService.getSpecialization(specializationId))
                .build();
    }

    @PutMapping("/{specializationId}")
    ApiResponse<SpecializationResponse> updateSpecialization(@PathVariable("specializationId") Long specializationId, @RequestBody SpecializationRequest request) {
        return ApiResponse.<SpecializationResponse>builder()
                .result(specializationService.updateSpecialization(specializationId, request))
                .build();
    }

    @DeleteMapping("/{specializationId}")
    ApiResponse<String> deleteSpecialization(@PathVariable Long specializationId) {
        specializationService.deleteSpecialization(specializationId);
        return ApiResponse.<String>builder().result("Specialization has been deleted").build();
    }
}
