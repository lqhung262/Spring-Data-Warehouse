package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Language.LanguageRequest;
import com.example.demo.dto.humanresource.Language.LanguageResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.LanguageService;
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
@RequestMapping("/api/v1/human-resource/languages")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LanguageController {
    LanguageService languageService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<LanguageResponse> createLanguage(@Valid @RequestBody LanguageRequest request) {
        ApiResponse<LanguageResponse> response = new ApiResponse<>();

        response.setResult(languageService.createLanguage(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertLanguages(
            @RequestBody List<LanguageRequest> requests) {
        log.info("Received bulk upsert request for {} languages", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("LANGUAGE", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.LANGUAGE_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "LANGUAGE", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteLanguages(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} languages", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("LANGUAGE", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.LANGUAGE_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "LANGUAGE", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<LanguageResponse>> getLanguages(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                     @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                     @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                     @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<LanguageResponse>>builder()
                .result(languageService.getLanguages(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{languageId}")
    ApiResponse<LanguageResponse> getLanguage(@PathVariable("languageId") Long languageId) {
        return ApiResponse.<LanguageResponse>builder()
                .result(languageService.getLanguage(languageId))
                .build();
    }

    @PutMapping("/{languageId}")
    ApiResponse<LanguageResponse> updateLanguage(@PathVariable("languageId") Long languageId, @RequestBody LanguageRequest request) {
        return ApiResponse.<LanguageResponse>builder()
                .result(languageService.updateLanguage(languageId, request))
                .build();
    }

    @DeleteMapping("/{languageId}")
    ApiResponse<String> deleteLanguage(@PathVariable Long languageId) {
        languageService.deleteLanguage(languageId);
        return ApiResponse.<String>builder().result("Language has been deleted").build();
    }
}
