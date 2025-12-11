package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityRequest;
import com.example.demo.dto.humanresource.OldProvinceCity.OldProvinceCityResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.OldProvinceCityService;
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
@RequestMapping("/api/v1/human-resource/old-province-cities")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OldProvinceCityController {
    OldProvinceCityService oldProvinceCityService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OldProvinceCityResponse> createOldProvinceCity(@Valid @RequestBody OldProvinceCityRequest request) {
        ApiResponse<OldProvinceCityResponse> response = new ApiResponse<>();

        response.setResult(oldProvinceCityService.createOldProvinceCity(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertOldProvinceCities(
            @RequestBody List<OldProvinceCityRequest> requests) {
        log.info("Received bulk upsert request for {} old province cities", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("OLD_PROVINCE_CITY", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.OLD_PROVINCE_CITY_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "OLD_PROVINCE_CITY", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteOldProvinceCities(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} old province cities", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("OLD_PROVINCE_CITY", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.OLD_PROVINCE_CITY_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "OLD_PROVINCE_CITY", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<OldProvinceCityResponse>> getOldProvinceCities(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                    @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                    @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                    @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<OldProvinceCityResponse>>builder()
                .result(oldProvinceCityService.getOldProvinceCities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{oldProvinceCityId}")
    ApiResponse<OldProvinceCityResponse> getOldProvinceCity(@PathVariable("oldProvinceCityId") Long oldProvinceCityId) {
        return ApiResponse.<OldProvinceCityResponse>builder()
                .result(oldProvinceCityService.getOldProvinceCity(oldProvinceCityId))
                .build();
    }

    @PutMapping("/{oldProvinceCityId}")
    ApiResponse<OldProvinceCityResponse> updateOldProvinceCity(@PathVariable("oldProvinceCityId") Long oldProvinceCityId, @RequestBody OldProvinceCityRequest request) {
        return ApiResponse.<OldProvinceCityResponse>builder()
                .result(oldProvinceCityService.updateOldProvinceCity(oldProvinceCityId, request))
                .build();
    }

    @DeleteMapping("/{oldProvinceCityId}")
    ApiResponse<String> deleteOldProvinceCity(@PathVariable Long oldProvinceCityId) {
        oldProvinceCityService.deleteOldProvinceCity(oldProvinceCityId);
        return ApiResponse.<String>builder().result("Old Province City has been deleted").build();
    }
}
