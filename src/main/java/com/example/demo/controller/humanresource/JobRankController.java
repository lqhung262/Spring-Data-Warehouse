package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
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
//    @PostMapping("/_bulk-upsert")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<List<JobRankResponse>> bulkUpsertJobRanks(
//            @Valid @RequestBody List<JobRankRequest> requests) {
//        return ApiResponse.<List<JobRankResponse>>builder()
//                .result(jobRankService.bulkUpsertJobRanks(requests))
//                .build();
//    }
//
//    /**
//     * BULK DELETE ENDPOINT
//     */
//    @DeleteMapping("/_bulk-delete")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<String> bulkDeleteJobRanks(@RequestParam("ids") List<Long> ids) {
//        jobRankService.bulkDeleteJobRanks(ids);
//        return ApiResponse.<String>builder()
//                .result(ids.size() + " job ranks have been deleted successfully")
//                .build();
//    }
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
