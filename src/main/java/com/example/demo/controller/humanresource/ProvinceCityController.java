package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityRequest;
import com.example.demo.dto.humanresource.ProvinceCity.ProvinceCityResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.ProvinceCityService;
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
@RequestMapping("/api/v1/human-resource/province-cities")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProvinceCityController {
    ProvinceCityService provinceCityService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<ProvinceCityResponse> createProvinceCity(@Valid @RequestBody ProvinceCityRequest request) {
        ApiResponse<ProvinceCityResponse> response = new ApiResponse<>();

        response.setResult(provinceCityService.createProvinceCity(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertProvinceCities(
            @RequestBody List<ProvinceCityRequest> requests) {
        log.info("Received bulk upsert request for {} province cities", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("PROVINCE_CITY", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.PROVINCE_CITY_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "PROVINCE_CITY", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteProvinceCities(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} province cities", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("PROVINCE_CITY", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.PROVINCE_CITY_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "PROVINCE_CITY", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<ProvinceCityResponse>> getProvinceCities(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                              @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                              @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                              @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<ProvinceCityResponse>>builder()
                .result(provinceCityService.getProvinceCities(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{provinceCityId}")
    ApiResponse<ProvinceCityResponse> getProvinceCity(@PathVariable("provinceCityId") Long provinceCityId) {
        return ApiResponse.<ProvinceCityResponse>builder()
                .result(provinceCityService.getProvinceCity(provinceCityId))
                .build();
    }

    @PutMapping("/{provinceCityId}")
    ApiResponse<ProvinceCityResponse> updateProvinceCity(@PathVariable("provinceCityId") Long provinceCityId, @RequestBody ProvinceCityRequest request) {
        return ApiResponse.<ProvinceCityResponse>builder()
                .result(provinceCityService.updateProvinceCity(provinceCityId, request))
                .build();
    }

    @DeleteMapping("/{provinceCityId}")
    ApiResponse<String> deleteProvinceCity(@PathVariable Long provinceCityId) {
        provinceCityService.deleteProvinceCity(provinceCityId);
        return ApiResponse.<String>builder().result(" ProvinceCity has been deleted").build();
    }
}
