package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Nationality.NationalityRequest;
import com.example.demo.dto.humanresource.Nationality.NationalityResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.NationalityService;
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
@RequestMapping("/api/v1/human-resource/nationalities")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NationalityController {
    NationalityService nationalityService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<NationalityResponse> createNationality(@Valid @RequestBody NationalityRequest request) {
        ApiResponse<NationalityResponse> response = new ApiResponse<>();

        response.setResult(nationalityService.createNationality(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertNationalities(
            @RequestBody List<NationalityRequest> requests) {
        log.info("Received bulk upsert request for {} nationalities", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("NATIONALITY", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.NATIONALITY_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "NATIONALITY", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteNationalities(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} nationalities", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("NATIONALITY", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.NATIONALITY_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "NATIONALITY", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<NationalityResponse>> getNationalities(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                            @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                            @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<NationalityResponse>>builder()
                .result(nationalityService.getNationalities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{nationalityId}")
    ApiResponse<NationalityResponse> getNationality(@PathVariable("nationalityId") Long nationalityId) {
        return ApiResponse.<NationalityResponse>builder()
                .result(nationalityService.getNationality(nationalityId))
                .build();
    }

    @PutMapping("/{nationalityId}")
    ApiResponse<NationalityResponse> updateNationality(@PathVariable("nationalityId") Long nationalityId, @RequestBody NationalityRequest request) {
        return ApiResponse.<NationalityResponse>builder()
                .result(nationalityService.updateNationality(nationalityId, request))
                .build();
    }

    @DeleteMapping("/{nationalityId}")
    ApiResponse<String> deleteNationality(@PathVariable Long nationalityId) {
        nationalityService.deleteNationality(nationalityId);
        return ApiResponse.<String>builder().result("Nationality has been deleted").build();
    }
}
