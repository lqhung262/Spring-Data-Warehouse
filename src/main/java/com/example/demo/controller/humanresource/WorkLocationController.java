package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.WorkLocation.WorkLocationRequest;
import com.example.demo.dto.humanresource.WorkLocation.WorkLocationResponse;
import com.example.demo.service.humanresource.WorkLocationService;
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
@RequestMapping("/work-locations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkLocationController {
    WorkLocationService workLocationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<WorkLocationResponse> createWorkLocation(@Valid @RequestBody WorkLocationRequest request) {
        ApiResponse<WorkLocationResponse> response = new ApiResponse<>();

        response.setResult(workLocationService.createWorkLocation(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
//    @PostMapping("/_bulk-upsert")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<List<WorkLocationResponse>> bulkUpsertWorkLocations(
//            @Valid @RequestBody List<WorkLocationRequest> requests) {
//        return ApiResponse.<List<WorkLocationResponse>>builder()
//                .result(workLocationService.bulkUpsertWorkLocations(requests))
//                .build();
//    }
//
//    /**
//     * BULK DELETE ENDPOINT
//     */
//    @DeleteMapping("/_bulk-delete")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<String> bulkDeleteWorkLocations(@RequestParam("ids") List<Long> ids) {
//        workLocationService.bulkDeleteWorkLocations(ids);
//        return ApiResponse.<String>builder()
//                .result(ids.size() + " work locations have been deleted successfully")
//                .build();
//    }
    @GetMapping()
    ApiResponse<List<WorkLocationResponse>> getWorkLocations(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                             @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<WorkLocationResponse>>builder()
                .result(workLocationService.getWorkLocations(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{workLocationId}")
    ApiResponse<WorkLocationResponse> getWorkLocation(@PathVariable("workLocationId") Long workLocationId) {
        return ApiResponse.<WorkLocationResponse>builder()
                .result(workLocationService.getWorkLocation(workLocationId))
                .build();
    }

    @PutMapping("/{workLocationId}")
    ApiResponse<WorkLocationResponse> updateWorkLocation(@PathVariable("workLocationId") Long workLocationId, @RequestBody WorkLocationRequest request) {
        return ApiResponse.<WorkLocationResponse>builder()
                .result(workLocationService.updateWorkLocation(workLocationId, request))
                .build();
    }

    @DeleteMapping("/{workLocationId}")
    ApiResponse<String> deleteWorkLocation(@PathVariable Long workLocationId) {
        workLocationService.deleteWorkLocation(workLocationId);
        return ApiResponse.<String>builder().result("Work Location has been deleted").build();
    }
}
