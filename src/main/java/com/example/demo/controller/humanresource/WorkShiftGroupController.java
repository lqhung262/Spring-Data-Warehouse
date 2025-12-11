package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupRequest;
import com.example.demo.dto.humanresource.WorkShiftGroup.WorkShiftGroupResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.WorkShiftGroupService;
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
@RequestMapping("/api/v1/human-resource/work-shift-groups")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkShiftGroupController {
    WorkShiftGroupService workShiftGroupService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<WorkShiftGroupResponse> createWorkShiftGroup(@Valid @RequestBody WorkShiftGroupRequest request) {
        ApiResponse<WorkShiftGroupResponse> response = new ApiResponse<>();

        response.setResult(workShiftGroupService.createWorkShiftGroup(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertWorkShiftGroups(
            @RequestBody List<WorkShiftGroupRequest> requests) {
        log.info("Received bulk upsert request for {} work shift groups", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("WORK_SHIFT_GROUP", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.WORK_SHIFT_GROUP_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "WORK_SHIFT_GROUP", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteWorkShiftGroups(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} work shift groups", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("WORK_SHIFT_GROUP", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.WORK_SHIFT_GROUP_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "WORK_SHIFT_GROUP", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<WorkShiftGroupResponse>> getWorkShiftGroups(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<WorkShiftGroupResponse>>builder()
                .result(workShiftGroupService.getWorkShiftGroups(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{workShiftGroupId}")
    ApiResponse<WorkShiftGroupResponse> getWorkShiftGroup(@PathVariable("workShiftGroupId") Long workShiftGroupId) {
        return ApiResponse.<WorkShiftGroupResponse>builder()
                .result(workShiftGroupService.getWorkShiftGroup(workShiftGroupId))
                .build();
    }

    @PutMapping("/{workShiftGroupId}")
    ApiResponse<WorkShiftGroupResponse> updateWorkShiftGroup(@PathVariable("workShiftGroupId") Long workShiftGroupId, @RequestBody WorkShiftGroupRequest request) {
        return ApiResponse.<WorkShiftGroupResponse>builder()
                .result(workShiftGroupService.updateWorkShiftGroup(workShiftGroupId, request))
                .build();
    }

    @DeleteMapping("/{workShiftGroupId}")
    ApiResponse<String> deleteWorkShiftGroup(@PathVariable Long workShiftGroupId) {
        workShiftGroupService.deleteWorkShiftGroup(workShiftGroupId);
        return ApiResponse.<String>builder().result("Work Shift Group has been deleted").build();
    }
}
