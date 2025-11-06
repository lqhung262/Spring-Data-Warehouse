package com.example.demo.controller.humanresource;


import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationResponse;
import com.example.demo.service.humanresource.EmployeeWorkLocationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee-work-locations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeWorkLocationController {
    EmployeeWorkLocationService employeeWorkLocationService;

    @PostMapping()
    ApiResponse<EmployeeWorkLocationResponse> createEmployeeWorkLocation(@Valid @RequestBody EmployeeWorkLocationRequest request) {
        ApiResponse<EmployeeWorkLocationResponse> response = new ApiResponse<>();

        response.setResult(employeeWorkLocationService.createEmployeeWorkLocation(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<EmployeeWorkLocationResponse>> getEmployeeWorkLocations(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                             @RequestParam(required = false, defaultValue = "employeeId") String sortBy,
                                                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EmployeeWorkLocationResponse>>builder()
                .result(employeeWorkLocationService.getEmployeeWorkLocations(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{employeeWorkLocationId}")
    ApiResponse<EmployeeWorkLocationResponse> getEmployeeWorkLocation(@PathVariable("employeeWorkLocationId") Long employeeWorkLocationId) {
        return ApiResponse.<EmployeeWorkLocationResponse>builder()
                .result(employeeWorkLocationService.getEmployeeWorkLocation(employeeWorkLocationId))
                .build();
    }

    @PutMapping("/{employeeWorkLocationId}")
    ApiResponse<EmployeeWorkLocationResponse> updateEmployeeWorkLocation(@PathVariable("employeeWorkLocationId") Long employeeWorkLocationId, @RequestBody EmployeeWorkLocationRequest request) {
        return ApiResponse.<EmployeeWorkLocationResponse>builder()
                .result(employeeWorkLocationService.updateEmployeeWorkLocation(employeeWorkLocationId, request))
                .build();
    }

    @DeleteMapping("/{employeeWorkLocationId}")
    ApiResponse<String> deleteEmployeeWorkLocation(@PathVariable("employeeWorkLocationId") Long id) {
        employeeWorkLocationService.deleteEmployeeWorkLocation(id);

        return ApiResponse.<String>builder().result("Employee Work Location has been deleted").build();
    }
}
