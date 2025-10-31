package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.JobTitle.JobTitleRequest;
import com.example.demo.dto.humanresource.JobTitle.JobTitleResponse;
import com.example.demo.service.humanresource.JobTitleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobTitles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JobTitleController {
    JobTitleService jobTitleService;

    @PostMapping()
    ApiResponse<JobTitleResponse> createJobTitle(@Valid @RequestBody JobTitleRequest request) {
        ApiResponse<JobTitleResponse> response = new ApiResponse<>();

        response.setResult(jobTitleService.createJobTitle(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<JobTitleResponse>> getJobTitles(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                     @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                     @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                     @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<JobTitleResponse>>builder()
                .result(jobTitleService.getJobTitles(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{jobTitleId}")
    ApiResponse<JobTitleResponse> getJobTitle(@PathVariable("jobTitleId") Long jobTitleId) {
        return ApiResponse.<JobTitleResponse>builder()
                .result(jobTitleService.getJobTitle(jobTitleId))
                .build();
    }

    @PutMapping("/{jobTitleId}")
    ApiResponse<JobTitleResponse> updateJobTitle(@PathVariable("jobTitleId") Long jobTitleId, @RequestBody JobTitleRequest request) {
        return ApiResponse.<JobTitleResponse>builder()
                .result(jobTitleService.updateJobTitle(jobTitleId, request))
                .build();
    }

    @DeleteMapping("/{jobTitleId}")
    ApiResponse<String> deleteJobTitle(@PathVariable Long jobTitleId) {
        jobTitleService.deleteJobTitle(jobTitleId);
        return ApiResponse.<String>builder().result("Job Title has been deleted").build();
    }
}
