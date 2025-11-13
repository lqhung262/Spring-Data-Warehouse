package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.Department.DepartmentRequest;
import com.example.demo.dto.humanresource.Department.DepartmentResponse;
import com.example.demo.service.humanresource.DepartmentService;
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
@RequestMapping("/departments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DepartmentController {
    DepartmentService departmentService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        ApiResponse<DepartmentResponse> response = new ApiResponse<>();

        response.setResult(departmentService.createDepartment(request));

        return response;
    }

    @PostMapping("/_bulk-upsert")
    ApiResponse<List<DepartmentResponse>> bulkDepartmentUpsert(@Valid @RequestBody List<DepartmentRequest> requests) {
        return ApiResponse.<List<DepartmentResponse>>builder()
                .result(departmentService.bulkUpsertDepartments(requests))
                .build();
    }

    @DeleteMapping("/_bulk-delete")
    public ApiResponse<String> bulkDeleteDepartments(@Valid @RequestParam("ids") List<Long> departmentIds) {
        departmentService.bulkDeleteDepartments(departmentIds);
        return ApiResponse.<String>builder()
                .result(departmentIds.size() + " departments have been deleted.")
                .build();
    }

    @GetMapping()
    ApiResponse<List<DepartmentResponse>> getDepartments(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                         @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                         @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                         @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<DepartmentResponse>>builder()
                .result(departmentService.getDepartments(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{departmentId}")
    ApiResponse<DepartmentResponse> getDepartment(@PathVariable("departmentId") Long departmentId) {
        return ApiResponse.<DepartmentResponse>builder()
                .result(departmentService.getDepartment(departmentId))
                .build();
    }

    @PutMapping("/{departmentId}")
    ApiResponse<DepartmentResponse> updateDepartment(@PathVariable("departmentId") Long departmentId, @RequestBody DepartmentRequest request) {
        return ApiResponse.<DepartmentResponse>builder()
                .result(departmentService.updateDepartment(departmentId, request))
                .build();
    }

    @DeleteMapping("/{departmentId}")
    ApiResponse<String> deleteDepartment(@PathVariable Long departmentId) {
        departmentService.deleteDepartment(departmentId);
        return ApiResponse.<String>builder().result("Department has been deleted").build();
    }
}
