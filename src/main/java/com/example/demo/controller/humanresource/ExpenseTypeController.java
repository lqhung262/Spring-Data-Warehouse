package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeRequest;
import com.example.demo.dto.humanresource.ExpenseType.ExpenseTypeResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.ExpenseTypeService;
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
@RequestMapping("/api/v1/human-resource/expense-types")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExpenseTypeController {
    ExpenseTypeService expenseTypeService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<ExpenseTypeResponse> createExpenseType(@Valid @RequestBody ExpenseTypeRequest request) {
        ApiResponse<ExpenseTypeResponse> response = new ApiResponse<>();

        response.setResult(expenseTypeService.createExpenseType(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertExpenseTypes(
            @RequestBody List<ExpenseTypeRequest> requests) {
        log.info("Received bulk upsert request for {} expense types", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("EXPENSE_TYPE", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.EXPENSE_TYPE_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "EXPENSE_TYPE", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteExpenseTypes(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} expense types", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("EXPENSE_TYPE", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.EXPENSE_TYPE_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "EXPENSE_TYPE", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<ExpenseTypeResponse>> getExpenseTypes(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                           @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                           @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                           @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<ExpenseTypeResponse>>builder()
                .result(expenseTypeService.getExpenseTypes(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{expenseTypeId}")
    ApiResponse<ExpenseTypeResponse> getExpenseType(@PathVariable("expenseTypeId") Long expenseTypeId) {
        return ApiResponse.<ExpenseTypeResponse>builder()
                .result(expenseTypeService.getExpenseType(expenseTypeId))
                .build();
    }

    @PutMapping("/{expenseTypeId}")
    ApiResponse<ExpenseTypeResponse> updateExpenseType(@PathVariable("expenseTypeId") Long expenseTypeId, @RequestBody ExpenseTypeRequest request) {
        return ApiResponse.<ExpenseTypeResponse>builder()
                .result(expenseTypeService.updateExpenseType(expenseTypeId, request))
                .build();
    }

    @DeleteMapping("/{expenseTypeId}")
    ApiResponse<String> deleteExpenseType(@PathVariable Long expenseTypeId) {
        expenseTypeService.deleteExpenseType(expenseTypeId);
        return ApiResponse.<String>builder().result("Expense Type has been deleted").build();
    }
}
