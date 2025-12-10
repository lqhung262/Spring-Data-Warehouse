package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Department.DepartmentRequest;
import com.example.demo.dto.humanresource.Department.DepartmentResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.DepartmentService;
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
@RequestMapping("/api/v1/human-resource/departments")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentController {
    DepartmentService departmentService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        ApiResponse<DepartmentResponse> response = new ApiResponse<>();

        response.setResult(departmentService.createDepartment(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertDepartments(
            @RequestBody List<DepartmentRequest> requests) {
        log.info("Received bulk upsert request for {} departments", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("DEPARTMENT", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.DEPARTMENT_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "DEPARTMENT", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteDepartments(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} departments", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("DEPARTMENT", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.DEPARTMENT_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "DEPARTMENT", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<DepartmentResponse>> getDepartments(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                         @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                         @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                         @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<DepartmentResponse>>builder()
                .result(departmentService.getDepartments(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{departmentId}")
    ApiResponse<DepartmentResponse> getDepartment(@PathVariable("departmentId") Long departmentId) {
        return ApiResponse.<DepartmentResponse>builder()
                .result(departmentService.getDepartment(departmentId))
                .build();
    }

    @PutMapping("/{departmentId}")
    ApiResponse<DepartmentResponse> updateDepartment(@PathVariable("departmentId") Long departmentId, @RequestBody DepartmentRequest request) {
        return ApiResponse.<DepartmentResponse>builder()
                .result(departmentService.updateDepartment(departmentId, request))
                .build();
    }

    @DeleteMapping("/{departmentId}")
    ApiResponse<String> deleteDepartment(@PathVariable Long departmentId) {
        departmentService.deleteDepartment(departmentId);
        return ApiResponse.<String>builder().result("Department has been deleted").build();
    }
}
