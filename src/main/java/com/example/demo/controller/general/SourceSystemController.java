package com.example.demo.controller.general;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.general.SourceSystem.SourceSystemRequest;
import com.example.demo.dto.general.SourceSystem.SourceSystemResponse;
import com.example.demo.service.general.SourceSystemService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/source-systems")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SourceSystemController {
    SourceSystemService sourceSystemService;

    @PostMapping()
    ApiResponse<SourceSystemResponse> createSourceSystem(@Valid @RequestBody SourceSystemRequest request) {
        ApiResponse<SourceSystemResponse> response = new ApiResponse<>();

        response.setResult(sourceSystemService.createSourceSystem(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<SourceSystemResponse>> getSourceSystems(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                             @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<SourceSystemResponse>>builder()
                .result(sourceSystemService.getSourceSystems(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{sourceSystemId}")
    ApiResponse<SourceSystemResponse> getSourceSystem(@PathVariable("sourceSystemId") Long sourceSystemId) {
        return ApiResponse.<SourceSystemResponse>builder()
                .result(sourceSystemService.getSourceSystem(sourceSystemId))
                .build();
    }

    @PutMapping("/{sourceSystemId}")
    ApiResponse<SourceSystemResponse> updateSourceSystem(@PathVariable("sourceSystemId") Long sourceSystemId, @RequestBody SourceSystemRequest request) {
        return ApiResponse.<SourceSystemResponse>builder()
                .result(sourceSystemService.updateSourceSystem(sourceSystemId, request))
                .build();
    }

    @DeleteMapping("/{sourceSystemId}")
    ApiResponse<String> deleteSourceSystem(@PathVariable Long sourceSystemId) {
        sourceSystemService.deleteSourceSystem(sourceSystemId);
        return ApiResponse.<String>builder().result("Source System has been deleted").build();
    }
}
