package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.OldWard.OldWardRequest;
import com.example.demo.dto.humanresource.OldWard.OldWardResponse;
import com.example.demo.service.humanresource.OldWardService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/old-wards")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OldWardController {
    OldWardService oldWardService;

    @PostMapping()
    ApiResponse<OldWardResponse> createOldWard(@Valid @RequestBody OldWardRequest request) {
        ApiResponse<OldWardResponse> response = new ApiResponse<>();

        response.setResult(oldWardService.createOldWard(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<OldWardResponse>> getOldWards(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                   @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                   @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                   @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<OldWardResponse>>builder()
                .result(oldWardService.getOldWards(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{oldWardId}")
    ApiResponse<OldWardResponse> getOldWard(@PathVariable("oldWardId") Long oldWardId) {
        return ApiResponse.<OldWardResponse>builder()
                .result(oldWardService.getOldWard(oldWardId))
                .build();
    }

    @PutMapping("/{oldWardId}")
    ApiResponse<OldWardResponse> updateOldWard(@PathVariable("oldWardId") Long oldWardId, @RequestBody OldWardRequest request) {
        return ApiResponse.<OldWardResponse>builder()
                .result(oldWardService.updateOldWard(oldWardId, request))
                .build();
    }

    @DeleteMapping("/{oldWardId}")
    ApiResponse<String> deleteOldWard(@PathVariable Long oldWardId) {
        oldWardService.deleteOldWard(oldWardId);
        return ApiResponse.<String>builder().result("Old Ward has been deleted").build();
    }
}
