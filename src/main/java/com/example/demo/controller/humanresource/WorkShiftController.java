package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftRequest;
import com.example.demo.dto.humanresource.WorkShift.WorkShiftResponse;
import com.example.demo.service.humanresource.WorkShiftService;
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
@RequestMapping("/work-shifts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WorkShiftController {
    WorkShiftService workShiftService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<WorkShiftResponse> createWorkShift(@Valid @RequestBody WorkShiftRequest request) {
        ApiResponse<WorkShiftResponse> response = new ApiResponse<>();

        response.setResult(workShiftService.createWorkShift(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
//    @PostMapping("/_bulk-upsert")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<List<WorkShiftResponse>> bulkUpsertWorkShifts(
//            @Valid @RequestBody List<WorkShiftRequest> requests) {
//        return ApiResponse.<List<WorkShiftResponse>>builder()
//                .result(workShiftService.bulkUpsertWorkShifts(requests))
//                .build();
//    }
//
//    /**
//     * BULK DELETE ENDPOINT
//     */
//    @DeleteMapping("/_bulk-delete")
//    @ResponseStatus(HttpStatus.OK)
//    ApiResponse<String> bulkDeleteWorkShifts(@RequestParam("ids") List<Long> ids) {
//        workShiftService.bulkDeleteWorkShifts(ids);
//        return ApiResponse.<String>builder()
//                .result(ids.size() + " work shifts have been deleted successfully")
//                .build();
//    }
    @GetMapping()
    ApiResponse<List<WorkShiftResponse>> getWorkShifts(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                       @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<WorkShiftResponse>>builder()
                .result(workShiftService.getWorkShifts(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{workShiftId}")
    ApiResponse<WorkShiftResponse> getWorkShift(@PathVariable("workShiftId") Long workShiftId) {
        return ApiResponse.<WorkShiftResponse>builder()
                .result(workShiftService.getWorkShift(workShiftId))
                .build();
    }

    @PutMapping("/{workShiftId}")
    ApiResponse<WorkShiftResponse> updateWorkShift(@PathVariable("workShiftId") Long workShiftId, @RequestBody WorkShiftRequest request) {
        return ApiResponse.<WorkShiftResponse>builder()
                .result(workShiftService.updateWorkShift(workShiftId, request))
                .build();
    }

    @DeleteMapping("/{workShiftId}")
    ApiResponse<String> deleteWorkShift(@PathVariable Long workShiftId) {
        workShiftService.deleteWorkShift(workShiftId);
        return ApiResponse.<String>builder().result("Work Shift  has been deleted").build();
    }
}
