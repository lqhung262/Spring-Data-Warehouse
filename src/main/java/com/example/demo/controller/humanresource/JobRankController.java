package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.JobRank.JobRankRequest;
import com.example.demo.dto.humanresource.JobRank.JobRankResponse;
import com.example.demo.service.humanresource.JobRankService;
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
@RequestMapping("/job-ranks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobRankController {
    JobRankService jobRankService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<JobRankResponse> createJobRank(@Valid @RequestBody JobRankRequest request) {
        ApiResponse<JobRankResponse> response = new ApiResponse<>();

        response.setResult(jobRankService.createJobRank(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<JobRankResponse>> bulkUpsertJobRanks(
            @Valid @RequestBody List<JobRankRequest> requests) {

        BulkOperationResult<JobRankResponse> result =
                jobRankService.bulkUpsertJobRanks(requests);

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

        return ApiResponse.<BulkOperationResult<JobRankResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteJobRanks(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = jobRankService.bulkDeleteJobRanks(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<JobRankResponse>> getJobRanks(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                   @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                   @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                   @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<JobRankResponse>>builder()
                .result(jobRankService.getJobRanks(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{jobRankId}")
    ApiResponse<JobRankResponse> getJobRank(@PathVariable("jobRankId") Long jobRankId) {
        return ApiResponse.<JobRankResponse>builder()
                .result(jobRankService.getJobRank(jobRankId))
                .build();
    }

    @PutMapping("/{jobRankId}")
    ApiResponse<JobRankResponse> updateJobRank(@PathVariable("jobRankId") Long jobRankId, @RequestBody JobRankRequest request) {
        return ApiResponse.<JobRankResponse>builder()
                .result(jobRankService.updateJobRank(jobRankId, request))
                .build();
    }

    @DeleteMapping("/{jobRankId}")
    ApiResponse<String> deleteJobRank(@PathVariable Long jobRankId) {
        jobRankService.deleteJobRank(jobRankId);
        return ApiResponse.<String>builder().result("Job Rank has been deleted").build();
    }
}
