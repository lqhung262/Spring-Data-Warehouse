package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftRequest;
import com.example.demo.dto.humanresource.EmployeeWorkShift.EmployeeWorkShiftResponse;
import com.example.demo.service.humanresource.EmployeeWorkShiftService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/human-resource/employee-work-shifts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeWorkShiftController {
    EmployeeWorkShiftService employeeWorkShiftService;

    @PostMapping()
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    ApiResponse<EmployeeWorkShiftResponse> createEmployeeWorkShift(@Valid @RequestBody EmployeeWorkShiftRequest request) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeWorkShift via Employee endpoints only");
    }

    @GetMapping()
    ApiResponse<List<EmployeeWorkShiftResponse>> getEmployeeWorkShifts(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                       @RequestParam(required = false, defaultValue = "employeeId") String sortBy,
                                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeWorkShift via Employee endpoints only");
    }

    @GetMapping("/{employeeWorkShiftId}")
    ApiResponse<EmployeeWorkShiftResponse> getEmployeeWorkShift(@PathVariable("employeeWorkShiftId") Long employeeWorkShiftId) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeWorkShift via Employee endpoints only");
    }

    @PutMapping("/{employeeWorkShiftId}")
    ApiResponse<EmployeeWorkShiftResponse> updateEmployeeWorkShift(@PathVariable("employeeWorkShiftId") Long employeeWorkShiftId, @RequestBody EmployeeWorkShiftRequest request) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeWorkShift via Employee endpoints only");
    }

    @DeleteMapping("/{employeeWorkShiftId}")
    ApiResponse<String> deleteEmployeeWorkShift(@PathVariable Long employeeWorkShiftId) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeWorkShift via Employee endpoints only");
    }
}
