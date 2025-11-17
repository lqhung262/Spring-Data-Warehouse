package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionRequest;
import com.example.demo.dto.humanresource.EmployeeDecision.EmployeeDecisionResponse;
import com.example.demo.service.humanresource.EmployeeDecisionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/employee-decisions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeDecisionController {
    EmployeeDecisionService employeeDecisionService;

    @PostMapping()
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    ApiResponse<EmployeeDecisionResponse> createEmployeeDecision(@Valid @RequestBody EmployeeDecisionRequest request) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeDecision via Employee endpoints only");
    }

    @GetMapping()
    ApiResponse<List<EmployeeDecisionResponse>> getEmployeeDecisions(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                     @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                     @RequestParam(required = false, defaultValue = "employeeId") String sortBy,
                                                                     @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeDecision via Employee endpoints only");
    }

    @GetMapping("/{employeeDecisionId}")
    ApiResponse<EmployeeDecisionResponse> getEmployeeDecision(@PathVariable("employeeDecisionId") Long employeeDecisionId) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeDecision via Employee endpoints only");
    }

    @PutMapping("/{employeeDecisionId}")
    ApiResponse<EmployeeDecisionResponse> updateEmployeeDecision(@PathVariable("employeeDecisionId") Long employeeDecisionId, @RequestBody EmployeeDecisionRequest request) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeDecision via Employee endpoints only");
    }

    @DeleteMapping("/{employeeDecisionId}")
    ApiResponse<String> deleteEmployeeDecision(@PathVariable Long employeeDecisionId) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeDecision via Employee endpoints only");
    }
}
