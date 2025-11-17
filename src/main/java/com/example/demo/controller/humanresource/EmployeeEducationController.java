package com.example.demo.controller.humanresource;


import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationResponse;
import com.example.demo.service.humanresource.EmployeeEducationService;
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
@RequestMapping("/employee-educations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeEducationController {
    EmployeeEducationService employeeEducationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<EmployeeEducationResponse> createEmployeeEducation(@Valid @RequestBody EmployeeEducationRequest request) {
        ApiResponse<EmployeeEducationResponse> response = new ApiResponse<>();

        response.setResult(employeeEducationService.createEmployeeEducation(request));

        return response;
    }

//    @PostMapping("/_bulk-upsert")
//    ApiResponse<List<EmployeeEducationResponse>> bulkEmployeeEducationUpsert(@Valid @RequestBody List<EmployeeEducationRequest> requests) {
//        return ApiResponse.<List<EmployeeEducationResponse>>builder()
//                .result(employeeEducationService.bulkUpsertEmployeeEducations(requests))
//                .build();
//    }
//
//    @DeleteMapping("/_bulk-delete")
//    public ApiResponse<String> bulkDeleteEmployeeEducations(@Valid @RequestParam("ids") List<Long> employeeEducationIds) {
//        employeeEducationService.bulkDeleteEmployeeEducations(employeeEducationIds);
//        return ApiResponse.<String>builder()
//                .result(employeeEducationIds.size() + " employee Educations have been deleted.")
//                .build();
//    }

    @GetMapping()
    ApiResponse<List<EmployeeEducationResponse>> getEmployeeEducations(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                       @RequestParam(required = false, defaultValue = "employeeId") String sortBy,
                                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EmployeeEducationResponse>>builder()
                .result(employeeEducationService.getEmployeeEducations(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{employeeEducationId}")
    ApiResponse<EmployeeEducationResponse> getEmployeeEducation(@PathVariable("employeeEducationId") Long employeeEducationId) {
        return ApiResponse.<EmployeeEducationResponse>builder()
                .result(employeeEducationService.getEmployeeEducation(employeeEducationId))
                .build();
    }

    @PutMapping("/{employeeEducationId}")
    ApiResponse<EmployeeEducationResponse> updateEmployeeEducation(@PathVariable("employeeEducationId") Long employeeEducationId, @RequestBody EmployeeEducationRequest request) {
        return ApiResponse.<EmployeeEducationResponse>builder()
                .result(employeeEducationService.updateEmployeeEducation(employeeEducationId, request))
                .build();
    }

    @DeleteMapping("/{employeeEducationId}")
    ApiResponse<String> deleteEmployeeEducation(@PathVariable("employeeEducationId") Long id) {
        employeeEducationService.deleteEmployeeEducation(id);

        return ApiResponse.<String>builder().result("Employee Education has been deleted").build();
    }
}
