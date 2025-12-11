package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.BloodGroup.BloodGroupRequest;
import com.example.demo.dto.humanresource.BloodGroup.BloodGroupResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.BloodGroupService;
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
@RequestMapping("/api/v1/human-resource/blood-groups")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BloodGroupController {
    BloodGroupService bloodGroupService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<BloodGroupResponse> createBloodGroup(@Valid @RequestBody BloodGroupRequest request) {
        ApiResponse<BloodGroupResponse> response = new ApiResponse<>();

        response.setResult(bloodGroupService.createBloodGroup(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertBloodGroups(
            @RequestBody List<BloodGroupRequest> requests) {
        log.info("Received bulk upsert request for {} blood groups", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("BLOOD_GROUP", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.BLOOD_GROUP_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "BLOOD_GROUP", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteBloodGroups(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} blood groups", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("BLOOD_GROUP", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.BLOOD_GROUP_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "BLOOD_GROUP", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<BloodGroupResponse>> getBloodGroups(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                         @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                         @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                         @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<BloodGroupResponse>>builder()
                .result(bloodGroupService.getBloodGroups(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{bloodGroupId}")
    ApiResponse<BloodGroupResponse> getBloodGroup(@PathVariable("bloodGroupId") Long bloodGroupId) {
        return ApiResponse.<BloodGroupResponse>builder()
                .result(bloodGroupService.getBloodGroup(bloodGroupId))
                .build();
    }

    @PutMapping("/{bloodGroupId}")
    ApiResponse<BloodGroupResponse> updateBloodGroup(@PathVariable("bloodGroupId") Long bloodGroupId, @RequestBody BloodGroupRequest request) {
        return ApiResponse.<BloodGroupResponse>builder()
                .result(bloodGroupService.updateBloodGroup(bloodGroupId, request))
                .build();
    }

    @DeleteMapping("/{bloodGroupId}")
    ApiResponse<String> deleteBloodGroup(@PathVariable Long bloodGroupId) {
        bloodGroupService.deleteBloodGroup(bloodGroupId);
        return ApiResponse.<String>builder().result("Blood Group has been deleted").build();
    }
}
