package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Major.MajorRequest;
import com.example.demo.dto.humanresource.Major.MajorResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.MajorService;
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
@RequestMapping("/api/v1/human-resource/majors")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MajorController {
    MajorService majorService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<MajorResponse> createMajor(@Valid @RequestBody MajorRequest request) {
        ApiResponse<MajorResponse> response = new ApiResponse<>();

        response.setResult(majorService.createMajor(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertMajors(
            @RequestBody List<MajorRequest> requests) {
        log.info("Received bulk upsert request for {} majors", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("MAJOR", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.MAJOR_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "MAJOR", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteMajors(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} majors", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("MAJOR", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.MAJOR_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "MAJOR", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<MajorResponse>> getMajors(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                               @RequestParam(required = false, defaultValue = "5") int pageSize,
                                               @RequestParam(required = false, defaultValue = "name") String sortBy,
                                               @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<MajorResponse>>builder()
                .result(majorService.getMajors(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{majorId}")
    ApiResponse<MajorResponse> getMajor(@PathVariable("majorId") Long majorId) {
        return ApiResponse.<MajorResponse>builder()
                .result(majorService.getMajor(majorId))
                .build();
    }

    @PutMapping("/{majorId}")
    ApiResponse<MajorResponse> updateMajor(@PathVariable("majorId") Long majorId, @RequestBody MajorRequest request) {
        return ApiResponse.<MajorResponse>builder()
                .result(majorService.updateMajor(majorId, request))
                .build();
    }

    @DeleteMapping("/{majorId}")
    ApiResponse<String> deleteMajor(@PathVariable Long majorId) {
        majorService.deleteMajor(majorId);
        return ApiResponse.<String>builder().result("Major has been deleted").build();
    }
}
