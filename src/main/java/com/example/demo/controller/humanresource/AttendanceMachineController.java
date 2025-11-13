package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineRequest;
import com.example.demo.dto.humanresource.AttendanceMachine.AttendanceMachineResponse;
import com.example.demo.service.humanresource.AttendanceMachineService;
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
@RequestMapping("/attendance-machines")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceMachineController {
    AttendanceMachineService attendanceMachineService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<AttendanceMachineResponse> createAttendanceMachine(@Valid @RequestBody AttendanceMachineRequest request) {
        ApiResponse<AttendanceMachineResponse> response = new ApiResponse<>();

        response.setResult(attendanceMachineService.createAttendanceMachine(request));

        return response;
    }

    @PostMapping("/_bulk-upsert")
    ApiResponse<List<AttendanceMachineResponse>> bulkAttendanceMachineUpsert(@Valid @RequestBody List<AttendanceMachineRequest> requests) {
        return ApiResponse.<List<AttendanceMachineResponse>>builder()
                .result(attendanceMachineService.bulkUpsertAttendanceMachines(requests))
                .build();
    }

    @DeleteMapping("/_bulk-delete")
    public ApiResponse<String> bulkDeleteAttendanceMachines(@Valid @RequestParam("ids") List<Long> attendanceMachineIds) {
        attendanceMachineService.bulkDeleteAttendanceMachines(attendanceMachineIds);
        return ApiResponse.<String>builder()
                .result(attendanceMachineIds.size() + " Attendance Machines have been deleted.")
                .build();
    }


    @GetMapping()
    ApiResponse<List<AttendanceMachineResponse>> getAttendanceMachines(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                       @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                       @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                       @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<AttendanceMachineResponse>>builder()
                .result(attendanceMachineService.getAttendanceMachines(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{attendanceMachineId}")
    ApiResponse<AttendanceMachineResponse> getAttendanceMachine(@PathVariable("attendanceMachineId") Long attendanceMachineId) {
        return ApiResponse.<AttendanceMachineResponse>builder()
                .result(attendanceMachineService.getAttendanceMachine(attendanceMachineId))
                .build();
    }

    @PutMapping("/{attendanceMachineId}")
    ApiResponse<AttendanceMachineResponse> updateAttendanceMachine(@PathVariable("attendanceMachineId") Long attendanceMachineId, @RequestBody AttendanceMachineRequest request) {
        return ApiResponse.<AttendanceMachineResponse>builder()
                .result(attendanceMachineService.updateAttendanceMachine(attendanceMachineId, request))
                .build();
    }

    @DeleteMapping("/{attendanceMachineId}")
    ApiResponse<String> deleteAttendanceMachine(@PathVariable Long attendanceMachineId) {
        attendanceMachineService.deleteAttendanceMachine(attendanceMachineId);
        return ApiResponse.<String>builder().result("Attendance Machine has been deleted").build();
    }
}
