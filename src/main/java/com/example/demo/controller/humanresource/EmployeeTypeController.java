package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeRequest;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.EmployeeTypeService;
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
@RequestMapping("/api/v1/human-resource/employee-types")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeTypeController {
    EmployeeTypeService employeeTypeService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<EmployeeTypeResponse> createEmployeeType(@Valid @RequestBody EmployeeTypeRequest request) {
        ApiResponse<EmployeeTypeResponse> response = new ApiResponse<>();

        response.setResult(employeeTypeService.createEmployeeType(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertEmployeeTypes(
            @RequestBody List<EmployeeTypeRequest> requests) {
        log.info("Received bulk upsert request for {} employee types", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("EMPLOYEE_TYPE", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.EMPLOYEE_TYPE_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "EMPLOYEE_TYPE", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteEmployeeTypes(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} employee types", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("EMPLOYEE_TYPE", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.EMPLOYEE_TYPE_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "EMPLOYEE_TYPE", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<EmployeeTypeResponse>> getEmployeeTypes(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                             @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EmployeeTypeResponse>>builder()
                .result(employeeTypeService.getEmployeeTypes(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{employeeTypeId}")
    ApiResponse<EmployeeTypeResponse> getEmployeeType(@PathVariable("employeeTypeId") Long employeeTypeId) {
        return ApiResponse.<EmployeeTypeResponse>builder()
                .result(employeeTypeService.getEmployeeType(employeeTypeId))
                .build();
    }

    @PutMapping("/{employeeTypeId}")
    ApiResponse<EmployeeTypeResponse> updateEmployeeType(@PathVariable("employeeTypeId") Long employeeTypeId, @RequestBody EmployeeTypeRequest request) {
        return ApiResponse.<EmployeeTypeResponse>builder()
                .result(employeeTypeService.updateEmployeeType(employeeTypeId, request))
                .build();
    }

    @DeleteMapping("/{employeeTypeId}")
    ApiResponse<String> deleteEmployeeType(@PathVariable Long employeeTypeId) {
        employeeTypeService.deleteEmployeeType(employeeTypeId);
        return ApiResponse.<String>builder().result("Employee Type has been deleted").build();
    }
}
