package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeRequest;
import com.example.demo.dto.humanresource.EmployeeType.EmployeeTypeResponse;
import com.example.demo.service.humanresource.EmployeeTypeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employeeTypes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeTypeController {
    EmployeeTypeService employeeTypeService;

    @PostMapping()
    ApiResponse<EmployeeTypeResponse> createEmployeeType(@Valid @RequestBody EmployeeTypeRequest request) {
        ApiResponse<EmployeeTypeResponse> response = new ApiResponse<>();

        response.setResult(employeeTypeService.createEmployeeType(request));

        return response;
    }

    @GetMapping()
    ApiResponse<List<EmployeeTypeResponse>> getEmployeeTypes(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                             @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EmployeeTypeResponse>>builder()
                .result(employeeTypeService.getEmployeeTypes(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{employeeTypeId}")
    ApiResponse<EmployeeTypeResponse> getEmployeeType(@PathVariable("employeeTypeId") Long employeeTypeId) {
        return ApiResponse.<EmployeeTypeResponse>builder()
                .result(employeeTypeService.getEmployeeType(employeeTypeId))
                .build();
    }

    @PutMapping("/{employeeTypeId}")
    ApiResponse<EmployeeTypeResponse> updateEmployeeType(@PathVariable("employeeTypeId") Long employeeTypeId, @RequestBody EmployeeTypeRequest request) {
        return ApiResponse.<EmployeeTypeResponse>builder()
                .result(employeeTypeService.updateEmployeeType(employeeTypeId, request))
                .build();
    }

    @DeleteMapping("/{employeeTypeId}")
    ApiResponse<String> deleteEmployeeType(@PathVariable Long employeeTypeId) {
        employeeTypeService.deleteEmployeeType(employeeTypeId);
        return ApiResponse.<String>builder().result("Employee Type has been deleted").build();
    }
}
