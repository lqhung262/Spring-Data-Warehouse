package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.LaborStatus.LaborStatusRequest;
import com.example.demo.dto.humanresource.LaborStatus.LaborStatusResponse;
import com.example.demo.service.humanresource.LaborStatusService;
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
@RequestMapping("/labor-statuses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LaborStatusController {
    LaborStatusService laborStatusService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<LaborStatusResponse> createLaborStatus(@Valid @RequestBody LaborStatusRequest request) {
        ApiResponse<LaborStatusResponse> response = new ApiResponse<>();

        response.setResult(laborStatusService.createLaborStatus(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
//    @PostMapping("/_bulk-upsert")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<List<LaborStatusResponse>> bulkUpsertLaborStatuses(
//            @Valid @RequestBody List<LaborStatusRequest> requests) {
//        return ApiResponse.<List<LaborStatusResponse>>builder()
//                .result(laborStatusService.bulkUpsertLaborStatuses(requests))
//                .build();
//    }
//
//    /**
//     * BULK DELETE ENDPOINT
//     */
//    @DeleteMapping("/_bulk-delete")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<String> bulkDeleteLaborStatuses(@RequestParam("ids") List<Long> ids) {
//        laborStatusService.bulkDeleteLaborStatuses(ids);
//        return ApiResponse.<String>builder()
//                .result(ids.size() + " labor statuses have been deleted successfully")
//                .build();
//    }
    @GetMapping()
    ApiResponse<List<LaborStatusResponse>> getLaborStatuses(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                            @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                            @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<LaborStatusResponse>>builder()
                .result(laborStatusService.getLaborStatuses(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{laborStatusId}")
    ApiResponse<LaborStatusResponse> getLaborStatus(@PathVariable("laborStatusId") Long laborStatusId) {
        return ApiResponse.<LaborStatusResponse>builder()
                .result(laborStatusService.getLaborStatus(laborStatusId))
                .build();
    }

    @PutMapping("/{laborStatusId}")
    ApiResponse<LaborStatusResponse> updateLaborStatus(@PathVariable("laborStatusId") Long laborStatusId, @RequestBody LaborStatusRequest request) {
        return ApiResponse.<LaborStatusResponse>builder()
                .result(laborStatusService.updateLaborStatus(laborStatusId, request))
                .build();
    }

    @DeleteMapping("/{laborStatusId}")
    ApiResponse<String> deleteLaborStatus(@PathVariable Long laborStatusId) {
        laborStatusService.deleteLaborStatus(laborStatusId);
        return ApiResponse.<String>builder().result("Labor Status has been deleted").build();
    }
}
