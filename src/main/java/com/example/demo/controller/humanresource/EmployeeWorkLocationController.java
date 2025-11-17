package com.example.demo.controller.humanresource;


import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationRequest;
import com.example.demo.dto.humanresource.EmployeeWorkLocation.EmployeeWorkLocationResponse;
import com.example.demo.service.humanresource.EmployeeWorkLocationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/employee-work-locations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeWorkLocationController {
    EmployeeWorkLocationService employeeWorkLocationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    ApiResponse<EmployeeWorkLocationResponse> createEmployeeWorkLocation(@Valid @RequestBody EmployeeWorkLocationRequest request) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeWorkLocation via Employee endpoints only");
    }

    @GetMapping()
    ApiResponse<List<EmployeeWorkLocationResponse>> getEmployeeWorkLocations(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                             @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                             @RequestParam(required = false, defaultValue = "employeeId") String sortBy,
                                                                             @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeWorkLocation via Employee endpoints only");
    }

    @GetMapping("/{employeeWorkLocationId}")
    ApiResponse<EmployeeWorkLocationResponse> getEmployeeWorkLocation(@PathVariable("employeeWorkLocationId") Long employeeWorkLocationId) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeWorkLocation via Employee endpoints only");
    }

    @PutMapping("/{employeeWorkLocationId}")
    ApiResponse<EmployeeWorkLocationResponse> updateEmployeeWorkLocation(@PathVariable("employeeWorkLocationId") Long employeeWorkLocationId, @RequestBody EmployeeWorkLocationRequest request) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeWorkLocation via Employee endpoints only");
    }

    @DeleteMapping("/{employeeWorkLocationId}")
    ApiResponse<String> deleteEmployeeWorkLocation(@PathVariable("employeeWorkLocationId") Long id) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeWorkLocation via Employee endpoints only");
    }
}
