package com.example.demo.controller.humanresource;


import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineResponse;
import com.example.demo.service.humanresource.EmployeeAttendanceMachineService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/human-resource/employee-attendance-machines")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeAttendanceMachineController {
    EmployeeAttendanceMachineService employeeAttendanceMachineService;

    @PostMapping()
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    ApiResponse<EmployeeAttendanceMachineResponse> createEmployeeAttendanceMachine(@Valid @RequestBody EmployeeAttendanceMachineRequest request) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeAttendanceMachine via Employee endpoints only");
    }

    @GetMapping()
    ApiResponse<List<EmployeeAttendanceMachineResponse>> getEmployeeAttendanceMachines(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                                       @RequestParam(required = false, defaultValue = "employeeId") String sortBy,
                                                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeAttendanceMachine via Employee endpoints only");
    }

    @GetMapping("/{employeeAttendanceMachineId}")
    ApiResponse<EmployeeAttendanceMachineResponse> getEmployeeAttendanceMachine(@PathVariable("employeeAttendanceMachineId") Long employeeAttendanceMachineId) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeAttendanceMachine via Employee endpoints only");
    }

    @PutMapping("/{employeeAttendanceMachineId}")
    ApiResponse<EmployeeAttendanceMachineResponse> updateEmployeeAttendanceMachine(@PathVariable("employeeAttendanceMachineId") Long employeeAttendanceMachineId, @RequestBody EmployeeAttendanceMachineRequest request) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeAttendanceMachine via Employee endpoints only");
    }

    @DeleteMapping("/{employeeAttendanceMachineId}")
    ApiResponse<String> deleteEmployeeAttendanceMachine(@PathVariable("employeeAttendanceMachineId") Long id) {
        throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, "Manage EmployeeAttendanceMachine via Employee endpoints only");
    }
}
