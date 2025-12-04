package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.JobPosition.JobPositionRequest;
import com.example.demo.dto.humanresource.JobPosition.JobPositionResponse;
import com.example.demo.service.humanresource.JobPositionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.controller.humanresource.AttendanceMachineController.getBulkOperationResultApiResponse;

@RestController
@RequestMapping("/job-positions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobPositionController {
    JobPositionService jobPositionService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<JobPositionResponse> createJobPosition(@Valid @RequestBody JobPositionRequest request) {
        ApiResponse<JobPositionResponse> response = new ApiResponse<>();

        response.setResult(jobPositionService.createJobPosition(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<JobPositionResponse>> bulkUpsertJobPositions(
            @Valid @RequestBody List<JobPositionRequest> requests) {

        BulkOperationResult<JobPositionResponse> result =
                jobPositionService.bulkUpsertJobPositions(requests);

        // Determine response code based on result
        int responseCode;
        if (!result.hasErrors()) {
            // Trường hợp 1: Không có lỗi nào -> Thành công toàn bộ
            responseCode = 1000;
        } else if (result.hasSuccess()) {
            // Trường hợp 2: Có lỗi NHƯNG cũng có thành công -> Thành công một phần (Multi-Status)
            responseCode = 207;
        } else {
            // Trường hợp 3: Có lỗi VÀ không có thành công nào -> Thất bại toàn bộ
            responseCode = 400;
        }

        return ApiResponse.<BulkOperationResult<JobPositionResponse>>builder()
                .code(responseCode)
                .message(result.getSummary())
                .result(result)
                .build();
    }

    /**
     * BULK DELETE
     */
    @DeleteMapping("/_bulk-delete")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<Long>> bulkDeleteJobPositions(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = jobPositionService.bulkDeleteJobPositions(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<JobPositionResponse>> getJobPositions(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                           @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                           @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                           @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<JobPositionResponse>>builder()
                .result(jobPositionService.getJobPositions(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{jobPositionId}")
    ApiResponse<JobPositionResponse> getJobPosition(@PathVariable("jobPositionId") Long jobPositionId) {
        return ApiResponse.<JobPositionResponse>builder()
                .result(jobPositionService.getJobPosition(jobPositionId))
                .build();
    }

    @PutMapping("/{jobPositionId}")
    ApiResponse<JobPositionResponse> updateJobPosition(@PathVariable("jobPositionId") Long jobPositionId, @RequestBody JobPositionRequest request) {
        return ApiResponse.<JobPositionResponse>builder()
                .result(jobPositionService.updateJobPosition(jobPositionId, request))
                .build();
    }

    @DeleteMapping("/{jobPositionId}")
    ApiResponse<String> deleteJobPosition(@PathVariable Long jobPositionId) {
        jobPositionService.deleteJobPosition(jobPositionId);
        return ApiResponse.<String>builder().result("Job Position has been deleted").build();
    }
}
