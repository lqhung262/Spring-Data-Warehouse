package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Employee.EmployeeRequest;
import com.example.demo.dto.humanresource.Employee.EmployeeResponse;
import com.example.demo.service.humanresource.EmployeeService;
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
@RequestMapping("/employees")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeController {
    EmployeeService employeeService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        ApiResponse<EmployeeResponse> response = new ApiResponse<>();

        response.setResult(employeeService.createEmployee(request));
        return response;
    }

//    @PostMapping("/_bulk-upsert")
//    ApiResponse<List<EmployeeResponse>> bulkEmployeeUpsert(@Valid @RequestBody List<EmployeeRequest> requests) {
//        return ApiResponse.<List<EmployeeResponse>>builder()
//                .result(employeeService.bulkUpsertEmployees(requests))
//                .build();
//    }
//
//    @DeleteMapping("/_bulk-delete")
//    public ApiResponse<String> bulkDeleteEmployees(@Valid @RequestParam("ids") List<Long> employeeIds) {
//        employeeService.bulkDeleteEmployees(employeeIds);
//        return ApiResponse.<String>builder()
//                .result(employeeIds.size() + " employees have been deleted.")
//                .build();
//    }

    @GetMapping()
    ApiResponse<List<EmployeeResponse>> getEmployees(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                     @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                     @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                     @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EmployeeResponse>>builder()
                .result(employeeService.getEmployees(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{employeeId}")
    ApiResponse<EmployeeResponse> getEmployee(@PathVariable("employeeId") Long employeeId) {
        //return employeeService.getEmployee(employeeId);
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.getEmployee(employeeId))
                .build();
    }

    @PutMapping("/{employeeId}")
    ApiResponse<EmployeeResponse> updateEmployee(@PathVariable("employeeId") Long employeeId, @RequestBody EmployeeRequest request) {
        //return employeeService.updateEmployee(employeeId, request);
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.updateEmployee(employeeId, request))
                .build();
    }

    @DeleteMapping("/{employeeId}")
    ApiResponse<String> deleteEmployee(@PathVariable Long employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ApiResponse.<String>builder().result("Employee has been deleted").build();
    }
}
