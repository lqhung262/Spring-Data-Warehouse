package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityRequest;
import com.example.demo.dto.humanresource.IdentityIssuingAuthority.IdentityIssuingAuthorityResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.IdentityIssuingAuthorityService;
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
@RequestMapping("/api/v1/human-resource/identity-issuing-authorities")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IdentityIssuingAuthorityController {
    IdentityIssuingAuthorityService identityIssuingAuthorityService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<IdentityIssuingAuthorityResponse> createIdentityIssuingAuthority(@Valid @RequestBody IdentityIssuingAuthorityRequest request) {
        ApiResponse<IdentityIssuingAuthorityResponse> response = new ApiResponse<>();

        response.setResult(identityIssuingAuthorityService.createIdentityIssuingAuthority(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertIdentityIssuingAuthorities(
            @RequestBody List<IdentityIssuingAuthorityRequest> requests) {
        log.info("Received bulk upsert request for {} identity issuing authorities", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("IDENTITY_ISSUING_AUTHORITY", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.IDENTITY_ISSUING_AUTHORITY_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "IDENTITY_ISSUING_AUTHORITY", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteIdentityIssuingAuthorities(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} identity issuing authorities", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("IDENTITY_ISSUING_AUTHORITY", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.IDENTITY_ISSUING_AUTHORITY_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "IDENTITY_ISSUING_AUTHORITY", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<IdentityIssuingAuthorityResponse>> getIdentityIssuingAuthorities(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                                      @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                                      @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                                      @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<IdentityIssuingAuthorityResponse>>builder()
                .result(identityIssuingAuthorityService.getIdentityIssuingAuthorities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{identityIssuingAuthorityId}")
    ApiResponse<IdentityIssuingAuthorityResponse> getIdentityIssuingAuthority(@PathVariable("identityIssuingAuthorityId") Long identityIssuingAuthorityId) {
        return ApiResponse.<IdentityIssuingAuthorityResponse>builder()
                .result(identityIssuingAuthorityService.getIdentityIssuingAuthority(identityIssuingAuthorityId))
                .build();
    }

    @PutMapping("/{identityIssuingAuthorityId}")
    ApiResponse<IdentityIssuingAuthorityResponse> updateIdentityIssuingAuthority(@PathVariable("identityIssuingAuthorityId") Long identityIssuingAuthorityId, @RequestBody IdentityIssuingAuthorityRequest request) {
        return ApiResponse.<IdentityIssuingAuthorityResponse>builder()
                .result(identityIssuingAuthorityService.updateIdentityIssuingAuthority(identityIssuingAuthorityId, request))
                .build();
    }

    @DeleteMapping("/{identityIssuingAuthorityId}")
    ApiResponse<String> deleteIdentityIssuingAuthority(@PathVariable Long identityIssuingAuthorityId) {
        identityIssuingAuthorityService.deleteIdentityIssuingAuthority(identityIssuingAuthorityId);
        return ApiResponse.<String>builder().result("Identity Issuing Authority has been deleted").build();
    }
}
