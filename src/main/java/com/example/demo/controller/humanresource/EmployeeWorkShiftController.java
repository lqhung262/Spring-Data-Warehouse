package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftResponse;
import com.example.demo.service.humanresource.EmployeeWorkShiftService;
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
@RequestMapping("/employee-work-shifts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeWorkShiftController {
    EmployeeWorkShiftService employeeWorkShiftService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<EmployeeWorkShiftResponse> createEmployeeWorkShift(@Valid @RequestBody EmployeeWorkShiftRequest request) {
        ApiResponse<EmployeeWorkShiftResponse> response = new ApiResponse<>();

        response.setResult(employeeWorkShiftService.createEmployeeWorkShift(request));

        return response;
    }

//    @PostMapping("/_bulk-upsert")
//    ApiResponse<List<EmployeeWorkShiftResponse>> bulkEmployeeWorkShiftUpsert(@Valid @RequestBody List<EmployeeWorkShiftRequest> requests) {
//        return ApiResponse.<List<EmployeeWorkShiftResponse>>builder()
//                .result(employeeWorkShiftService.bulkUpsertEmployeeWorkShifts(requests))
//                .build();
//    }
//
//    @DeleteMapping("/_bulk-delete")
//    public ApiResponse<String> bulkDeleteEmployeeWorkShifts(@Valid @RequestParam("ids") List<Long> employeeWorkShiftIds) {
//        employeeWorkShiftService.bulkDeleteEmployeeWorkShifts(employeeWorkShiftIds);
//        return ApiResponse.<String>builder()
//                .result(employeeWorkShiftIds.size() + " employee Work Shifts have been deleted.")
//                .build();
//    }

    @GetMapping()
    ApiResponse<List<EmployeeWorkShiftResponse>> getEmployeeWorkShifts(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                       @RequestParam(required = false, defaultValue = "employeeId") String sortBy,
                                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EmployeeWorkShiftResponse>>builder()
                .result(employeeWorkShiftService.getEmployeeWorkShifts(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{employeeWorkShiftId}")
    ApiResponse<EmployeeWorkShiftResponse> getEmployeeWorkShift(@PathVariable("employeeWorkShiftId") Long employeeWorkShiftId) {
        return ApiResponse.<EmployeeWorkShiftResponse>builder()
                .result(employeeWorkShiftService.getEmployeeWorkShift(employeeWorkShiftId))
                .build();
    }

    @PutMapping("/{employeeWorkShiftId}")
    ApiResponse<EmployeeWorkShiftResponse> updateEmployeeWorkShift(@PathVariable("employeeWorkShiftId") Long employeeWorkShiftId, @RequestBody EmployeeWorkShiftRequest request) {
        return ApiResponse.<EmployeeWorkShiftResponse>builder()
                .result(employeeWorkShiftService.updateEmployeeWorkShift(employeeWorkShiftId, request))
                .build();
    }

    @DeleteMapping("/{employeeWorkShiftId}")
    ApiResponse<String> deleteEmployeeWorkShift(@PathVariable Long employeeWorkShiftId) {
        employeeWorkShiftService.deleteEmployeeWorkShift(employeeWorkShiftId);
        return ApiResponse.<String>builder().result("Employee Work Shift has been deleted").build();
    }
}
