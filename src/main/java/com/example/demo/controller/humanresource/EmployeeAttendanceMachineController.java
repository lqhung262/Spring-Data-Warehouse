package com.example.demo.controller.humanresource;


import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineRequest;
import com.example.demo.dto.humanresource.EmployeeAttendanceMachine.EmployeeAttendanceMachineResponse;
import com.example.demo.service.humanresource.EmployeeAttendanceMachineService;
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
@RequestMapping("/employee-attendance-machines")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeAttendanceMachineController {
    EmployeeAttendanceMachineService employeeAttendanceMachineService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<EmployeeAttendanceMachineResponse> createEmployeeAttendanceMachine(@Valid @RequestBody EmployeeAttendanceMachineRequest request) {
        ApiResponse<EmployeeAttendanceMachineResponse> response = new ApiResponse<>();

        response.setResult(employeeAttendanceMachineService.createEmployeeAttendanceMachine(request));

        return response;
    }

    @PostMapping("/_bulk-upsert")
    ApiResponse<List<EmployeeAttendanceMachineResponse>> bulkEmployeeAttendanceMachineUpsert(@Valid @RequestBody List<EmployeeAttendanceMachineRequest> requests) {
        return ApiResponse.<List<EmployeeAttendanceMachineResponse>>builder()
                .result(employeeAttendanceMachineService.bulkUpsertEmployeeAttendanceMachines(requests))
                .build();
    }

    @DeleteMapping("/_bulk-delete")
    public ApiResponse<String> bulkDeleteEmployeeAttendanceMachines(@Valid @RequestParam("ids") List<Long> employeeAttedanceMachineIds) {
        employeeAttendanceMachineService.bulkDeleteEmployeeAttendanceMachines(employeeAttedanceMachineIds);
        return ApiResponse.<String>builder()
                .result(employeeAttedanceMachineIds.size() + " employee Attendance Machines have been deleted.")
                .build();
    }

    @GetMapping()
    ApiResponse<List<EmployeeAttendanceMachineResponse>> getEmployeeAttendanceMachines(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                                       @RequestParam(required = false, defaultValue = "employeeId") String sortBy,
                                                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<EmployeeAttendanceMachineResponse>>builder()
                .result(employeeAttendanceMachineService.getEmployeeAttendanceMachines(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{employeeAttendanceMachineId}")
    ApiResponse<EmployeeAttendanceMachineResponse> getEmployeeAttendanceMachine(@PathVariable("employeeAttendanceMachineId") Long employeeAttendanceMachineId) {
        return ApiResponse.<EmployeeAttendanceMachineResponse>builder()
                .result(employeeAttendanceMachineService.getEmployeeAttendanceMachine(employeeAttendanceMachineId))
                .build();
    }

    @PutMapping("/{employeeAttendanceMachineId}")
    ApiResponse<EmployeeAttendanceMachineResponse> updateEmployeeAttendanceMachine(@PathVariable("employeeAttendanceMachineId") Long employeeAttendanceMachineId, @RequestBody EmployeeAttendanceMachineRequest request) {
        return ApiResponse.<EmployeeAttendanceMachineResponse>builder()
                .result(employeeAttendanceMachineService.updateEmployeeAttendanceMachine(employeeAttendanceMachineId, request))
                .build();
    }

    @DeleteMapping("/{employeeAttendanceMachineId}")
    ApiResponse<String> deleteEmployeeAttendanceMachine(@PathVariable("employeeAttendanceMachineId") Long id) {
        employeeAttendanceMachineService.deleteEmployeeAttendanceMachine(id);

        return ApiResponse.<String>builder().result("Employee Attendance Machine has been deleted").build();
    }
}
