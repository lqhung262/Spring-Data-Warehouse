package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.EmployeeService;
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
@RequestMapping("/api/v1/human-resource/employees")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeController {
    EmployeeService employeeService;
    KafkaProducerService kafkaProducerService;
    KafkaJobStatusService jobStatusService;


    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        ApiResponse<EmployeeResponse> response = new ApiResponse<>();

        response.setResult(employeeService.createEmployee(request));
        return response;
    }

    /**
     * Bulk Upsert Employees - sends to Kafka
     * Returns HTTP 202 Accepted immediately
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertEmployees(@RequestBody List<EmployeeRequest> requests) {
        log.info("Received bulk upsert request for {} employees", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("EMPLOYEE", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.EMPLOYEE_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "EMPLOYEE", OperationType.UPSERT, requests.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk upsert request accepted")
                .result(response)
                .build();
    }

    /**
     * Bulk Delete Employees - sends to Kafka
     * Returns HTTP 202 Accepted immediately
     */
    @DeleteMapping("/bulk-delete")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkDeleteEmployees(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} employees", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("EMPLOYEE", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.EMPLOYEE_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "EMPLOYEE", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<EmployeeResponse>> getEmployees(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                     @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                     @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                     @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EmployeeResponse>>builder()
                .result(employeeService.getEmployees(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{employeeId}")
    ApiResponse<EmployeeResponse> getEmployee(@PathVariable("employeeId") Long employeeId) {
        //return employeeService.getEmployee(employeeId);
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.getEmployee(employeeId))
                .build();
    }

    @PutMapping("/{employeeId}")
    ApiResponse<EmployeeResponse> updateEmployee(@PathVariable("employeeId") Long employeeId, @RequestBody EmployeeRequest request) {
        //return employeeService.updateEmployee(employeeId, request);
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.updateEmployee(employeeId, request))
                .build();
    }

    @DeleteMapping("/{employeeId}")
    ApiResponse<String> deleteEmployee(@PathVariable Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ApiResponse.<String>builder().result("Employee has been deleted").build();
    }
}
