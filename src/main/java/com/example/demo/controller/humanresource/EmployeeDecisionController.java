package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionResponse;
import com.example.demo.service.humanresource.EmployeeDecisionService;
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
@RequestMapping("/employee-decisions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeDecisionController {
    EmployeeDecisionService employeeDecisionService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<EmployeeDecisionResponse> createEmployeeDecision(@Valid @RequestBody EmployeeDecisionRequest request) {
        ApiResponse<EmployeeDecisionResponse> response = new ApiResponse<>();

        response.setResult(employeeDecisionService.createEmployeeDecision(request));
        return response;
    }

//    @PostMapping("/_bulk-upsert")
//    ApiResponse<List<EmployeeDecisionResponse>> bulkEmployeeDecisionUpsert(@Valid @RequestBody List<EmployeeDecisionRequest> requests) {
//        return ApiResponse.<List<EmployeeDecisionResponse>>builder()
//                .result(employeeDecisionService.bulkUpsertEmployeeDecisions(requests))
//                .build();
//    }
//
//    @DeleteMapping("/_bulk-delete")
//    public ApiResponse<String> bulkDeleteEmployeeDecisions(@Valid @RequestParam("ids") List<Long> employeeDecisionIds) {
//        employeeDecisionService.bulkDeleteEmployeeDecisions(employeeDecisionIds);
//        return ApiResponse.<String>builder()
//                .result(employeeDecisionIds.size() + " employee Decisions have been deleted.")
//                .build();
//    }

    @GetMapping()
    ApiResponse<List<EmployeeDecisionResponse>> getEmployeeDecisions(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                     @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                     @RequestParam(required = false, defaultValue = "employeeId") String sortBy,
                                                                     @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EmployeeDecisionResponse>>builder()
                .result(employeeDecisionService.getEmployeeDecisions(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{employeeDecisionId}")
    ApiResponse<EmployeeDecisionResponse> getEmployeeDecision(@PathVariable("employeeDecisionId") Long employeeDecisionId) {
        return ApiResponse.<EmployeeDecisionResponse>builder()
                .result(employeeDecisionService.getEmployeeDecision(employeeDecisionId))
                .build();
    }

    @PutMapping("/{employeeDecisionId}")
    ApiResponse<EmployeeDecisionResponse> updateEmployeeDecision(@PathVariable("employeeDecisionId") Long employeeDecisionId, @RequestBody EmployeeDecisionRequest request) {
        return ApiResponse.<EmployeeDecisionResponse>builder()
                .result(employeeDecisionService.updateEmployeeDecision(employeeDecisionId, request))
                .build();
    }

    @DeleteMapping("/{employeeDecisionId}")
    ApiResponse<String> deleteEmployeeDecision(@PathVariable Long employeeDecisionId) {
        employeeDecisionService.deleteEmployeeDecision(employeeDecisionId);
        return ApiResponse.<String>builder().result("Employee Decision has been deleted").build();
    }
}
