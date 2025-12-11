package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftRequest;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.WorkShiftService;
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
@RequestMapping("/api/v1/human-resource/work-shifts")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkShiftController {
    WorkShiftService workShiftService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<WorkShiftResponse> createWorkShift(@Valid @RequestBody WorkShiftRequest request) {
        ApiResponse<WorkShiftResponse> response = new ApiResponse<>();

        response.setResult(workShiftService.createWorkShift(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertWorkShifts(
            @RequestBody List<WorkShiftRequest> requests) {
        log.info("Received bulk upsert request for {} work shifts", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("WORK_SHIFT", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.WORK_SHIFT_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "WORK_SHIFT", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteWorkShifts(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} work shifts", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("WORK_SHIFT", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.WORK_SHIFT_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "WORK_SHIFT", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<WorkShiftResponse>> getWorkShifts(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                       @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<WorkShiftResponse>>builder()
                .result(workShiftService.getWorkShifts(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{workShiftId}")
    ApiResponse<WorkShiftResponse> getWorkShift(@PathVariable("workShiftId") Long workShiftId) {
        return ApiResponse.<WorkShiftResponse>builder()
                .result(workShiftService.getWorkShift(workShiftId))
                .build();
    }

    @PutMapping("/{workShiftId}")
    ApiResponse<WorkShiftResponse> updateWorkShift(@PathVariable("workShiftId") Long workShiftId, @RequestBody WorkShiftRequest request) {
        return ApiResponse.<WorkShiftResponse>builder()
                .result(workShiftService.updateWorkShift(workShiftId, request))
                .build();
    }

    @DeleteMapping("/{workShiftId}")
    ApiResponse<String> deleteWorkShift(@PathVariable Long workShiftId) {
        workShiftService.deleteWorkShift(workShiftId);
        return ApiResponse.<String>builder().result("Work Shift  has been deleted").build();
    }
}
