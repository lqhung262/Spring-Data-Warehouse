package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusRequest;
import com.example.demo.dto.humanresource.MaritalStatus.MaritalStatusResponse;
import com.example.demo.service.humanresource.MaritalStatusService;
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
@RequestMapping("/marital-statuses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MaritalStatusController {
    MaritalStatusService maritalStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<MaritalStatusResponse> createMaritalStatus(@Valid @RequestBody MaritalStatusRequest request) {
        ApiResponse<MaritalStatusResponse> response = new ApiResponse<>();

        response.setResult(maritalStatusService.createMaritalStatus(request));

        return response;
    }

    @PostMapping("/_bulk-upsert")
    ApiResponse<List<MaritalStatusResponse>> bulkMaritalStatusUpsert(@Valid @RequestBody List<MaritalStatusRequest> requests) {
        return ApiResponse.<List<MaritalStatusResponse>>builder()
                .result(maritalStatusService.bulkUpsertMaritalStatuses(requests))
                .build();
    }

    @DeleteMapping("/_bulk-delete")
    public ApiResponse<String> bulkDeleteMaritalStatuses(@Valid @RequestParam("ids") List<Long> maritalStatusIds) {
        maritalStatusService.bulkDeleteMaritalStatuses(maritalStatusIds);
        return ApiResponse.<String>builder()
                .result(maritalStatusIds.size() + " marital Statuses have been deleted.")
                .build();
    }

    @GetMapping()
    ApiResponse<List<MaritalStatusResponse>> getMaritalStatuss(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                               @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                               @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                               @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<MaritalStatusResponse>>builder()
                .result(maritalStatusService.getMaritalStatuses(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{maritalStatusId}")
    ApiResponse<MaritalStatusResponse> getMaritalStatus(@PathVariable("maritalStatusId") Long maritalStatusId) {
        return ApiResponse.<MaritalStatusResponse>builder()
                .result(maritalStatusService.getMaritalStatus(maritalStatusId))
                .build();
    }

    @PutMapping("/{maritalStatusId}")
    ApiResponse<MaritalStatusResponse> updateMaritalStatus(@PathVariable("maritalStatusId") Long maritalStatusId, @RequestBody MaritalStatusRequest request) {
        return ApiResponse.<MaritalStatusResponse>builder()
                .result(maritalStatusService.updateMaritalStatus(maritalStatusId, request))
                .build();
    }

    @DeleteMapping("/{maritalStatusId}")
    ApiResponse<String> deleteMaritalStatus(@PathVariable Long maritalStatusId) {
        maritalStatusService.deleteMaritalStatus(maritalStatusId);
        return ApiResponse.<String>builder().result("Marital Status has been deleted").build();
    }
}
