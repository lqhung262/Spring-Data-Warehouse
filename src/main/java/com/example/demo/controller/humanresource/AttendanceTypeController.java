package com.example.demo.controller.humanresource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.BulkOperationResult;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeRequest;
import com.example.demo.dto.humanresource.AttendanceType.AttendanceTypeResponse;
import com.example.demo.service.humanresource.AttendanceTypeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.controller.humanresource.AttendanceMachineController.getBulkOperationResultApiResponse;

@RestController
@RequestMapping("/attendance-types")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceTypeController {
    AttendanceTypeService attendanceTypeService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<AttendanceTypeResponse> createAttendanceType(@Valid @RequestBody AttendanceTypeRequest request) {
        ApiResponse<AttendanceTypeResponse> response = new ApiResponse<>();

        response.setResult(attendanceTypeService.createAttendanceType(request));

        return response;
    }

    /**
     * BULK UPSERT ENDPOINT
     */
    @PostMapping("/_bulk-upsert")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<BulkOperationResult<AttendanceTypeResponse>> bulkUpsertAttendanceTypes(
            @Valid @RequestBody List<AttendanceTypeRequest> requests) {

        BulkOperationResult<AttendanceTypeResponse> result =
                attendanceTypeService.bulkUpsertAttendanceTypes(requests);

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

        return ApiResponse.<BulkOperationResult<AttendanceTypeResponse>>builder()
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
    ApiResponse<BulkOperationResult<Long>> bulkDeleteAttendanceTypes(@RequestParam("ids") List<Long> ids) {

        BulkOperationResult<Long> result = attendanceTypeService.bulkDeleteAttendanceTypes(ids);

        // Determine response code
        return getBulkOperationResultApiResponse(result);
    }

    @GetMapping()
    ApiResponse<List<AttendanceTypeResponse>> getAttendanceTypes(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                                 @RequestParam(required = false, defaultValue = "5") int pageSize,
                                                                 @RequestParam(required = false, defaultValue = "name") String sortBy,
                                                                 @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        Sort sort = null;
        if (sortDirection.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else {
            sort = Sort.by(sortBy).descending();
        }

        return ApiResponse.<List<AttendanceTypeResponse>>builder()
                .result(attendanceTypeService.getAttendanceTypes(PageRequest.of(pageNo - 1, pageSize, sort)))
                .build();
    }

    @GetMapping("/{attendanceTypeId}")
    ApiResponse<AttendanceTypeResponse> getAttendanceType(@PathVariable("attendanceTypeId") Long attendanceTypeId) {
        return ApiResponse.<AttendanceTypeResponse>builder()
                .result(attendanceTypeService.getAttendanceType(attendanceTypeId))
                .build();
    }

    @PutMapping("/{attendanceTypeId}")
    ApiResponse<AttendanceTypeResponse> updateAttendanceType(@PathVariable("attendanceTypeId") Long attendanceTypeId, @RequestBody AttendanceTypeRequest request) {
        return ApiResponse.<AttendanceTypeResponse>builder()
                .result(attendanceTypeService.updateAttendanceType(attendanceTypeId, request))
                .build();
    }

    @DeleteMapping("/{attendanceTypeId}")
    ApiResponse<String> deleteAttendanceType(@PathVariable Long attendanceTypeId) {
        attendanceTypeService.deleteAttendanceType(attendanceTypeId);
        return ApiResponse.<String>builder().result("Attendance Type  has been deleted").build();
    }
}
