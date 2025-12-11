package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictRequest;
import com.example.demo.dto.humanresource.OldDistrict.OldDistrictResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.OldDistrictService;
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
@RequestMapping("/api/v1/human-resource/old-districts")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OldDistrictController {
    OldDistrictService oldDistrictService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OldDistrictResponse> createOldDistrict(@Valid @RequestBody OldDistrictRequest request) {
        ApiResponse<OldDistrictResponse> response = new ApiResponse<>();

        response.setResult(oldDistrictService.createOldDistrict(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertOldDistricts(
            @RequestBody List<OldDistrictRequest> requests) {
        log.info("Received bulk upsert request for {} old districts", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("OLD_DISTRICT", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.OLD_DISTRICT_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "OLD_DISTRICT", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteOldDistricts(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} old districts", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("OLD_DISTRICT", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.OLD_DISTRICT_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "OLD_DISTRICT", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<OldDistrictResponse>> getOldDistricts(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                           @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                           @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                           @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        return ApiResponse.<List<OldDistrictResponse>>builder()
                .result(oldDistrictService.getOldDistricts(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{oldDistrictId}")
    ApiResponse<OldDistrictResponse> getOldDistrict(@PathVariable("oldDistrictId") Long oldDistrictId) {
        return ApiResponse.<OldDistrictResponse>builder()
                .result(oldDistrictService.getOldDistrict(oldDistrictId))
                .build();
    }

    @PutMapping("/{oldDistrictId}")
    ApiResponse<OldDistrictResponse> updateOldDistrict(@PathVariable("oldDistrictId") Long oldDistrictId, @RequestBody OldDistrictRequest request) {
        return ApiResponse.<OldDistrictResponse>builder()
                .result(oldDistrictService.updateOldDistrict(oldDistrictId, request))
                .build();
    }

    @DeleteMapping("/{oldDistrictId}")
    ApiResponse<String> deleteOldDistrict(@PathVariable Long oldDistrictId) {
        oldDistrictService.deleteOldDistrict(oldDistrictId);
        return ApiResponse.<String>builder().result("Old District has been deleted").build();
    }
}
