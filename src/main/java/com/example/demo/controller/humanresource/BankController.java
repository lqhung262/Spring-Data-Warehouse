package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Bank.BankRequest;
import com.example.demo.dto.humanresource.Bank.BankResponse;
import com.example.demo.dto.kafka.JobSubmissionResponse;
import com.example.demo.kafka.enums.DataDomain;
import com.example.demo.kafka.enums.MessageSpec;
import com.example.demo.kafka.enums.OperationType;
import com.example.demo.kafka.producer.KafkaProducerService;
import com.example.demo.kafka.service.KafkaJobStatusService;
import com.example.demo.service.humanresource.BankService;
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
@RequestMapping("/api/v1/human-resource/banks")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BankController {
    BankService bankService;
    final KafkaProducerService kafkaProducerService;
    final KafkaJobStatusService jobStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
        // 201 Created
    ApiResponse<BankResponse> createBank(@Valid @RequestBody BankRequest request) {
        ApiResponse<BankResponse> response = new ApiResponse<>();

        response.setResult(bankService.createBank(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/bulk-upsert")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<JobSubmissionResponse> bulkUpsertBanks(
            @RequestBody List<BankRequest> requests) {
        log.info("Received bulk upsert request for {} banks", requests.size());

        // Create job
        String jobId = jobStatusService.createJob("BANK", OperationType.UPSERT, requests.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, requests, MessageSpec.BANK_UPSERT, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "BANK", OperationType.UPSERT, requests.size());

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
    public ApiResponse<JobSubmissionResponse> bulkDeleteBanks(@RequestBody List<Long> ids) {
        log.info("Received bulk delete request for {} banks", ids.size());

        // Create job
        String jobId = jobStatusService.createJob("BANK", OperationType.DELETE, ids.size());

        // Send to Kafka
        kafkaProducerService.sendToOriginalTopic(jobId, ids, MessageSpec.BANK_DELETE, DataDomain.HUMAN_RESOURCE.getValue());

        // Create response
        JobSubmissionResponse response = jobStatusService.createSubmissionResponse(
                jobId, "BANK", OperationType.DELETE, ids.size());

        return ApiResponse.<JobSubmissionResponse>builder()
                .code(HttpStatus.ACCEPTED.value())
                .message("Bulk delete request accepted")
                .result(response)
                .build();
    }

    @GetMapping()
    ApiResponse<List<BankResponse>> getBanks(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                             @RequestParam(required = false, defaultValue = "name") String sortBy,
                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<BankResponse>>builder()
                .result(bankService.getBanks(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{bankId}")
    ApiResponse<BankResponse> getBank(@PathVariable("bankId") Long bankId) {
        return ApiResponse.<BankResponse>builder()
                .result(bankService.getBank(bankId))
                .build();
    }

    @PutMapping("/{bankId}")
    ApiResponse<BankResponse> updateBank(@PathVariable("bankId") Long bankId, @RequestBody BankRequest request) {
        return ApiResponse.<BankResponse>builder()
                .result(bankService.updateBank(bankId, request))
                .build();
    }

    @DeleteMapping("/{bankId}")
    ApiResponse<String> deleteBank(@PathVariable Long bankId) {
        bankService.deleteBank(bankId);
        return ApiResponse.<String>builder().result("Bank has been deleted").build();
    }
}
