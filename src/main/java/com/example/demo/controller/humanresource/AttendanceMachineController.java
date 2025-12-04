package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
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

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<AttendanceMachineResponse>> bulkUpsertAttendanceMachines(
            @Valid @RequestBody List<AttendanceMachineRequest> requests) {

        BulkOperationResult<AttendanceMachineResponse> result =
                attendanceMachineService.bulkUpsertAttendanceMachines(requests);

        // Determine response code based on result
        int responseCode;
        if (!result.hasErrors()) {
            // Trường hợp 1: Không có lỗi nào -> Thành công toàn bộ
            responseCode = 1000;
        } else if (result.hasSuccess()) {
            // Trường hợp 2: Có lỗi NHƯNG cũng có thành công -> Thành công một phần (Multi-Status)
            responseCode = 207;
        } else {
            // Trường hợp 3: Có lỗi VÀ không có thành công nào -> Thất bại toàn bộ
            responseCode = 400;
        }

        return ApiResponse.<BulkOperationResult<AttendanceMachineResponse>>builder()
                .code(responseCode)
                .message(result.getSummary())
                .result(result)
                .build();
    }

    /**
     * BULK DELETE
     */
    @DeleteMapping("/_bulk-delete")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<Long>> bulkDeleteAttendanceMachines(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = attendanceMachineService.bulkDeleteAttendanceMachines(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    static ApiResponse<BulkOperationResult<Long>> getBulkOperationResultApiResponse(BulkOperationResult<Long> result) {
        int responseCode;
        if (!result.hasErrors()) {
            responseCode = 1000; // All succeeded
        } else if (result.hasSuccess()) {
            responseCode = 207;  // Partial success (Multi-Status)
        } else {
            responseCode = 400;  // All failed
        }

        return ApiResponse.<BulkOperationResult<Long>>builder()
                .code(responseCode)
                .message(result.getSummary())
                .result(result)
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
