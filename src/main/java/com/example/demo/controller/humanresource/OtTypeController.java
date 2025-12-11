package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.OtType.OtTypeRequest;
import com.example.demo.dto.humanresource.OtType.OtTypeResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.OtTypeService;
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
@RequestMapping("/api/v1/human-resource/ot-types")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtTypeController {
    OtTypeService otTypeService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OtTypeResponse> createOtType(@Valid @RequestBody OtTypeRequest request) {
        ApiResponse<OtTypeResponse> response = new ApiResponse<>();

        response.setResult(otTypeService.createOtType(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertOtTypes(
            @RequestBody List<OtTypeRequest> requests) {
        log.info("Received bulk upsert request for {} OT types", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("OT_TYPE", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.OT_TYPE_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "OT_TYPE", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteOtTypes(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} OT types", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("OT_TYPE", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.OT_TYPE_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "OT_TYPE", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<OtTypeResponse>> getOtTypes(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<OtTypeResponse>>builder()
                .result(otTypeService.getOtTypes(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{otTypeId}")
    ApiResponse<OtTypeResponse> getOtType(@PathVariable("otTypeId") Long otTypeId) {
        return ApiResponse.<OtTypeResponse>builder()
                .result(otTypeService.getOtType(otTypeId))
                .build();
    }

    @PutMapping("/{otTypeId}")
    ApiResponse<OtTypeResponse> updateOtType(@PathVariable("otTypeId") Long otTypeId, @RequestBody OtTypeRequest request) {
        return ApiResponse.<OtTypeResponse>builder()
                .result(otTypeService.updateOtType(otTypeId, request))
                .build();
    }

    @DeleteMapping("/{otTypeId}")
    ApiResponse<String> deleteOtType(@PathVariable Long otTypeId) {
        otTypeService.deleteOtType(otTypeId);
        return ApiResponse.<String>builder().result("Ot Type has been deleted").build();
    }
}
