package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityRequest;
import com.example.demo.dto.humanresource.MedicalFacility.MedicalFacilityResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.MedicalFacilityService;
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
@RequestMapping("/api/v1/human-resource/medical-facilities")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MedicalFacilityController {
    MedicalFacilityService medicalFacilityService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<MedicalFacilityResponse> createMedicalFacility(@Valid @RequestBody MedicalFacilityRequest request) {
        ApiResponse<MedicalFacilityResponse> response = new ApiResponse<>();

        response.setResult(medicalFacilityService.createMedicalFacility(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertMedicalFacilities(
            @RequestBody List<MedicalFacilityRequest> requests) {
        log.info("Received bulk upsert request for {} medical facilities", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("MEDICAL_FACILITY", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.MEDICAL_FACILITY_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "MEDICAL_FACILITY", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteMedicalFacilities(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} medical facilities", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("MEDICAL_FACILITY", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.MEDICAL_FACILITY_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "MEDICAL_FACILITY", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<MedicalFacilityResponse>> getMedicalFacilities(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                    @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                    @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                    @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<MedicalFacilityResponse>>builder()
                .result(medicalFacilityService.getMedicalFacilities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{medicalFacilityId}")
    ApiResponse<MedicalFacilityResponse> getMedicalFacility(@PathVariable("medicalFacilityId") Long medicalFacilityId) {
        return ApiResponse.<MedicalFacilityResponse>builder()
                .result(medicalFacilityService.getMedicalFacility(medicalFacilityId))
                .build();
    }

    @PutMapping("/{medicalFacilityId}")
    ApiResponse<MedicalFacilityResponse> updateMedicalFacility(@PathVariable("medicalFacilityId") Long medicalFacilityId, @RequestBody MedicalFacilityRequest request) {
        return ApiResponse.<MedicalFacilityResponse>builder()
                .result(medicalFacilityService.updateMedicalFacility(medicalFacilityId, request))
                .build();
    }

    @DeleteMapping("/{medicalFacilityId}")
    ApiResponse<String> deleteMedicalFacility(@PathVariable Long medicalFacilityId) {
        medicalFacilityService.deleteMedicalFacility(medicalFacilityId);
        return ApiResponse.<String>builder().result("Medical Facility has been deleted").build();
    }
}
