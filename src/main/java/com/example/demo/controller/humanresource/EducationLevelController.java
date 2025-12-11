package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelRequest;
import com.example.demo.dto.humanresource.EducationLevel.EducationLevelResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.EducationLevelService;
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
@RequestMapping("/api/v1/human-resource/education-levels")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EducationLevelController {
    EducationLevelService educationLevelService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<EducationLevelResponse> createEducationLevel(@Valid @RequestBody EducationLevelRequest request) {
        ApiResponse<EducationLevelResponse> response = new ApiResponse<>();

        response.setResult(educationLevelService.createEducationLevel(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertEducationLevels(
            @RequestBody List<EducationLevelRequest> requests) {
        log.info("Received bulk upsert request for {} education levels", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("EDUCATION_LEVEL", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.EDUCATION_LEVEL_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "EDUCATION_LEVEL", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteEducationLevels(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} education levels", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("EDUCATION_LEVEL", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.EDUCATION_LEVEL_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "EDUCATION_LEVEL", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<EducationLevelResponse>> getEducationLevels(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EducationLevelResponse>>builder()
                .result(educationLevelService.getEducationLevels(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{educationLevelId}")
    ApiResponse<EducationLevelResponse> getEducationLevel(@PathVariable("educationLevelId") Long educationLevelId) {
        return ApiResponse.<EducationLevelResponse>builder()
                .result(educationLevelService.getEducationLevel(educationLevelId))
                .build();
    }

    @PutMapping("/{educationLevelId}")
    ApiResponse<EducationLevelResponse> updateEducationLevel(@PathVariable("educationLevelId") Long educationLevelId, @RequestBody EducationLevelRequest request) {
        return ApiResponse.<EducationLevelResponse>builder()
                .result(educationLevelService.updateEducationLevel(educationLevelId, request))
                .build();
    }

    @DeleteMapping("/{educationLevelId}")
    ApiResponse<String> deleteEducationLevel(@PathVariable Long educationLevelId) {
        educationLevelService.deleteEducationLevel(educationLevelId);
        return ApiResponse.<String>builder().result("Education Level has been deleted").build();
    }
}
