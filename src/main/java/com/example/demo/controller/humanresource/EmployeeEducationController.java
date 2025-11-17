package com.example.demo.controller.humanresource;


import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationRequest;
import com.example.demo.dto.humanresource.EmployeeEducation.EmployeeEducationResponse;
import com.example.demo.service.humanresource.EmployeeEducationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/employee-educations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeEducationController {
    EmployeeEducationService employeeEducationService;

    // All operations on this resource are disallowed. Manage via Employee endpoints only.
    @PostMapping()
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    ApiResponse<EmployeeEducationResponse> createEmployeeEducation(@Valid @RequestBody EmployeeEducationRequest request) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeEducation via Employee endpoints only");
    }

    @GetMapping()
    ApiResponse<List<EmployeeEducationResponse>> getEmployeeEducations(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                       @RequestParam(required = false, defaultValue = "employeeId") String sortBy,
                                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeEducation via Employee endpoints only");
    }

    @GetMapping("/{employeeEducationId}")
    ApiResponse<EmployeeEducationResponse> getEmployeeEducation(@PathVariable("employeeEducationId") Long employeeEducationId) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeEducation via Employee endpoints only");
    }

    @PutMapping("/{employeeEducationId}")
    ApiResponse<EmployeeEducationResponse> updateEmployeeEducation(@PathVariable("employeeEducationId") Long employeeEducationId, @RequestBody EmployeeEducationRequest request) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeEducation via Employee endpoints only");
    }

    @DeleteMapping("/{employeeEducationId}")
    ApiResponse<String> deleteEmployeeEducation(@PathVariable("employeeEducationId") Long id) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeEducation via Employee endpoints only");
    }
}
